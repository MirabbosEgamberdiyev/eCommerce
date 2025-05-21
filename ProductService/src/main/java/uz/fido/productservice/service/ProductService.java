package uz.fido.productservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import uz.fido.productservice.client.InventoryClient;
import uz.fido.productservice.client.UserClient;
import uz.fido.productservice.config.RabbitMQConfig;
import uz.fido.productservice.dto.NotificationRequest;
import uz.fido.productservice.dto.ProductDto;
import uz.fido.productservice.dto.UserDto;
import uz.fido.productservice.exceptions.ProductNotFoundException;
import uz.fido.productservice.model.Inventory;
import uz.fido.productservice.model.Product;
import uz.fido.productservice.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final InventoryClient inventoryClient;
    private final UserClient userClient;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public Product addProduct(ProductDto productDto) {
        logger.debug("Adding product: {}", productDto.getName());

        // Validate input
        if (productDto.getPrice() < 0) {
            logger.warn("Invalid price: {}", productDto.getPrice());
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (productDto.getUserEmail() == null || productDto.getUserEmail().isEmpty()) {
            logger.warn("User email is missing");
            throw new IllegalArgumentException("User email is required");
        }

        // Verify user email exists via auth-service
        try {
            UserDto user = userClient.getUserByEmail(productDto.getUserEmail());
            if (user.getEmail() == null || user.getEmail().isEmpty()) {
                logger.warn("User email not found: {}", productDto.getUserEmail());
                throw new IllegalStateException("User email not found");
            }
            logger.debug("Verified user email: {}", user.getEmail());
        } catch (FeignException e) {
            logger.error("Failed to verify user email {}: {}", productDto.getUserEmail(), e.getMessage());
            throw new RuntimeException("Auth service unavailable", e);
        }

        // Create product
        Product product = new Product();
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setProductCode(generateProductCode(productDto.getName()));
        product = productRepository.save(product);
        logger.info("Product added with ID: {}", product.getId());

        // Initialize inventory
        try {
            Inventory inventory = new Inventory();
            inventory.setProductCode(product.getProductCode());
            inventory.setQuantity(0); // Initial stock
            inventoryClient.createInventory(inventory);
            logger.debug("Inventory initialized for productCode: {}", product.getProductCode());
        } catch (FeignException e) {
            logger.warn("Failed to initialize inventory for productCode {}: {}", product.getProductCode(), e.getMessage());
            // Continue despite inventory failure
        }

        // Send notification via RabbitMQ
        try {
            NotificationRequest notificationRequest = new NotificationRequest(
                    productDto.getUserEmail(),
                    "New Product Added",
                    "A new product '" + product.getName() + "' with ID " + product.getId() + " has been added."
            );
            String message = objectMapper.writeValueAsString(notificationRequest);
            rabbitTemplate.convertAndSend(RabbitMQConfig.NOTIFICATION_EXCHANGE, RabbitMQConfig.ROUTING_KEY, message);
            logger.debug("Notification sent to RabbitMQ for product ID: {}", product.getId());
        } catch (Exception e) {
            logger.warn("Failed to send notification for product ID {}: {}", product.getId(), e.getMessage());
            // Continue despite notification failure
        }

        return product;
    }

    public List<Product> getAllProducts() {
        logger.debug("Retrieving all products");
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(String id) {
        logger.debug("Retrieving product by ID: {}", id);
        return productRepository.findById(id);
    }

    public Product updateProduct(String id, ProductDto productDto) {
        logger.debug("Updating product with ID: {}", id);

        // Validate input
        if (productDto.getPrice() < 0) {
            logger.warn("Invalid price: {}", productDto.getPrice());
            throw new IllegalArgumentException("Price cannot be negative");
        }

        // Update product
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Product not found for ID: {}", id);
                    return new ProductNotFoundException("Product not found with ID: " + id);
                });
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product = productRepository.save(product);
        logger.info("Product updated with ID: {}", id);

        return product;
    }

    public void deleteProduct(String id) {
        logger.debug("Deleting product with ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Product not found for ID: {}", id);
                    return new ProductNotFoundException("Product not found with ID: " + id);
                });

        // Assume admin email for deletion (or fetch from context)
        String adminEmail = "admin@example.com"; // Replace with actual admin email or fetch from context
        try {
            UserDto user = userClient.getUserByEmail(adminEmail);
            if (user.getEmail() == null || user.getEmail().isEmpty()) {
                logger.warn("Admin email not found: {}", adminEmail);
                throw new IllegalStateException("Admin email not found");
            }
            logger.debug("Verified admin email: {}", user.getEmail());
        } catch (FeignException e) {
            logger.error("Failed to verify admin email {}: {}", adminEmail, e.getMessage());
            throw new RuntimeException("Auth service unavailable", e);
        }

        productRepository.deleteById(id);
        logger.info("Product deleted with ID: {}", id);

        // Send notification via RabbitMQ
        try {
            NotificationRequest notificationRequest = new NotificationRequest(
                    adminEmail,
                    "Product Deleted",
                    "The product '" + product.getName() + "' with ID " + id + " has been deleted."
            );
            String message = objectMapper.writeValueAsString(notificationRequest);
            rabbitTemplate.convertAndSend(RabbitMQConfig.NOTIFICATION_EXCHANGE, RabbitMQConfig.ROUTING_KEY, message);
            logger.debug("Notification sent to RabbitMQ for deleted product ID: {}", id);
        } catch (Exception e) {
            logger.warn("Failed to send notification for deleted product ID {}: {}", id, e.getMessage());
            // Continue despite notification failure
        }
    }

    private String generateProductCode(String name) {
        return name.toUpperCase().replaceAll("\\s+", "") + "-" + System.currentTimeMillis();
    }
}