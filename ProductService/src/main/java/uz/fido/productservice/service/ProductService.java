package uz.fido.productservice.service;

import org.springframework.stereotype.Service;
import uz.fido.productservice.client.InventoryClient;
import uz.fido.productservice.exceptions.ProductNotFoundException;
import uz.fido.productservice.model.Inventory;
import uz.fido.productservice.model.Product;
import uz.fido.productservice.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.fido.productservice.dto.ProductDto;

@Service
@RequiredArgsConstructor
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final InventoryClient inventoryClient;

    public Product addProduct(ProductDto productDto) {
        logger.debug("Adding product: {}", productDto.getName());

        // Validate input
        if (productDto.getPrice() < 0) {
            logger.warn("Invalid price: {}", productDto.getPrice());
            throw new IllegalArgumentException("Price cannot be negative");
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
        if (!productRepository.existsById(id)) {
            logger.warn("Product not found for ID: {}", id);
            throw new ProductNotFoundException("Product not found with ID: " + id);
        }
        productRepository.deleteById(id);
        logger.info("Product deleted with ID: {}", id);
    }

    private String generateProductCode(String name) {
        return name.toUpperCase().replaceAll("\\s+", "") + "-" + System.currentTimeMillis();
    }
}