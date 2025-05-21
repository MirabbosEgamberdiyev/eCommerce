package uz.fido.orderservice.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import uz.fido.orderservice.client.InventoryClient;
import uz.fido.orderservice.config.RabbitMQConfig;
import uz.fido.orderservice.dto.NotificationRequest;
import uz.fido.orderservice.dto.OrderRequest;
import uz.fido.orderservice.model.Order;
import uz.fido.orderservice.repository.OrderRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public Order createOrder(OrderRequest orderRequest) {
        logger.debug("Creating order for productId: {}", orderRequest.getProductId());

        // Validate input
        if (orderRequest.getQuantity() <= 0) {
            logger.warn("Invalid quantity: {}", orderRequest.getQuantity());
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (orderRequest.getTotalPrice() < 0) {
            logger.warn("Invalid totalPrice: {}", orderRequest.getTotalPrice());
            throw new IllegalArgumentException("Total price cannot be negative");
        }

        // Check inventory stock
        try {
            Boolean isInStock = inventoryClient.isInStock(orderRequest.getProductId());
            if (!isInStock) {
                logger.warn("Product out of stock: {}", orderRequest.getProductId());
                throw new IllegalStateException("Product is out of stock");
            }
        } catch (FeignException e) {
            logger.error("Failed to check inventory for productId {}: {}", orderRequest.getProductId(), e.getMessage());
            throw new RuntimeException("Inventory service unavailable", e);
        }

        // Create order
        Order order = new Order();
        order.setProductId(orderRequest.getProductId());
        order.setQuantity(orderRequest.getQuantity());
        order.setTotalPrice(orderRequest.getTotalPrice());
        order = orderRepository.save(order);
        logger.info("Order created with ID: {}", order.getId());

        // Send notification via RabbitMQ
        try {
            NotificationRequest notificationRequest = new NotificationRequest(
                    "customer@example.com", // Replace with actual user email
                    "Order Confirmation",
                    "Your order with ID " + order.getId() + " has been placed successfully."
            );
            String message = objectMapper.writeValueAsString(notificationRequest);
            rabbitTemplate.convertAndSend(RabbitMQConfig.NOTIFICATION_EXCHANGE, RabbitMQConfig.ROUTING_KEY, message);
            logger.debug("Notification sent to RabbitMQ for order ID: {}", order.getId());
        } catch (Exception e) {
            logger.warn("Failed to send notification for order ID {}: {}", order.getId(), e.getMessage());
            // Continue despite notification failure
        }

        return order;
    }

    public Optional<Order> getOrderById(String id) {
        logger.debug("Retrieving order by ID: {}", id);
        return orderRepository.findById(id);
    }

    public List<Order> getAllOrders() {
        logger.debug("Retrieving all orders");
        return orderRepository.findAll();
    }

    public Order updateOrder(String id, OrderRequest orderRequest) {
        logger.debug("Updating order with ID: {}", id);

        // Validate input
        if (orderRequest.getQuantity() <= 0) {
            logger.warn("Invalid quantity: {}", orderRequest.getQuantity());
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (orderRequest.getTotalPrice() < 0) {
            logger.warn("Invalid totalPrice: {}", orderRequest.getTotalPrice());
            throw new IllegalArgumentException("Total price cannot be negative");
        }

        // Update order
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Order not found for ID: {}", id);
                    return new IllegalArgumentException("Order not found");
                });
        order.setProductId(orderRequest.getProductId());
        order.setQuantity(orderRequest.getQuantity());
        order.setTotalPrice(orderRequest.getTotalPrice());
        order = orderRepository.save(order);
        logger.info("Order updated with ID: {}", id);
        return order;
    }

    public void deleteOrder(String id) {
        logger.debug("Deleting order with ID: {}", id);
        if (!orderRepository.existsById(id)) {
            logger.warn("Order not found for ID: {}", id);
            throw new IllegalArgumentException("Order not found");
        }
        orderRepository.deleteById(id);
        logger.info("Order deleted with ID: {}", id);
    }
}