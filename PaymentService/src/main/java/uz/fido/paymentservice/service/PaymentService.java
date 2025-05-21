package uz.fido.paymentservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import uz.fido.paymentservice.clients.OrderClient;
import uz.fido.paymentservice.config.RabbitMQConfig;
import uz.fido.paymentservice.dto.NotificationRequest;
import uz.fido.paymentservice.dto.OrderResponse;
import uz.fido.paymentservice.dto.PaymentRequest;
import uz.fido.paymentservice.exceptions.PaymentException;
import uz.fido.paymentservice.model.Payment;
import uz.fido.paymentservice.repository.PaymentRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final OrderClient orderClient;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    private static final String[] VALID_PAYMENT_METHODS = {"CREDIT_CARD", "PAYPAL", "CASH"};
    private static final String[] VALID_STATUSES = {"PENDING", "COMPLETED", "FAILED"};

    public Payment processPayment(PaymentRequest request) {
        logger.debug("Processing payment for orderId: {}", request.getOrderId());

        // Validate input
        if (request.getAmount() <= 0) {
            logger.warn("Invalid amount: {}", request.getAmount());
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (!Arrays.asList(VALID_PAYMENT_METHODS).contains(request.getPaymentMethod())) {
            logger.warn("Invalid payment method: {}", request.getPaymentMethod());
            throw new IllegalArgumentException("Invalid payment method");
        }

        // Validate order exists and amount matches
        try {
            OrderResponse order = orderClient.getOrderById(request.getOrderId());
            if (!order.getTotalPrice().equals(request.getAmount())) {
                logger.warn("Payment amount {} does not match order total {}", request.getAmount(), order.getTotalPrice());
                throw new PaymentException("Payment amount does not match order total");
            }
        } catch (FeignException e) {
            logger.error("Failed to validate order {}: {}", request.getOrderId(), e.getMessage());
            throw new PaymentException("Order validation failed", e);
        }

        // Simulate third-party payment processing
        String status = simulatePaymentProcessing(request);
        logger.debug("Payment status for orderId {}: {}", request.getOrderId(), status);

        // Save payment
        Payment payment = new Payment();
        payment.setOrderId(request.getOrderId());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus(status);
        payment.setPaymentDate(LocalDateTime.now());
        payment = paymentRepository.save(payment);
        logger.info("Payment processed with ID: {}", payment.getId());

        // Send notification via RabbitMQ
        if ("COMPLETED".equals(status)) {
            try {
                NotificationRequest notificationRequest = new NotificationRequest(
                        "customer@example.com", // Replace with actual user email
                        "Payment Confirmation",
                        "Your payment of $" + payment.getAmount() + " for order " + payment.getOrderId() + " was successful."
                );
                String message = objectMapper.writeValueAsString(notificationRequest);
                rabbitTemplate.convertAndSend(RabbitMQConfig.NOTIFICATION_EXCHANGE, RabbitMQConfig.ROUTING_KEY, message);
                logger.debug("Notification sent to RabbitMQ for payment ID: {}", payment.getId());
            } catch (Exception e) {
                logger.warn("Failed to send notification for payment ID {}: {}", payment.getId(), e.getMessage());
                // Continue despite notification failure
            }
        }

        return payment;
    }

    public Optional<Payment> getPaymentByOrderId(String orderId) {
        logger.debug("Retrieving payment by orderId: {}", orderId);
        return paymentRepository.findByOrderId(orderId);
    }

    private String simulatePaymentProcessing(PaymentRequest request) {
        try {
            Thread.sleep(1000); // Simulate network delay
            return "COMPLETED";
        } catch (InterruptedException e) {
            logger.error("Payment processing failed for orderId {}: {}", request.getOrderId(), e.getMessage());
            return "FAILED";
        }
    }
}