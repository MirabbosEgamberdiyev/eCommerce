package uz.fido.orderservice.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.fido.orderservice.dto.OrderRequest;
import uz.fido.orderservice.dto.OrderResponse;
import uz.fido.orderservice.model.Order;
import uz.fido.orderservice.service.OrderService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        logger.debug("Creating order for productId: {}", orderRequest.getProductId());
        Order order = orderService.createOrder(orderRequest);
        return new ResponseEntity<>(new OrderResponse(order), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable String id) {
        logger.debug("Fetching order by ID: {}", id);
        Optional<Order> order = orderService.getOrderById(id);
        return order.map(o -> ResponseEntity.ok(new OrderResponse(o)))
                .orElseGet(() -> {
                    logger.warn("Order not found for ID: {}", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                });
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        logger.debug("Fetching all orders");
        List<OrderResponse> orders = orderService.getAllOrders().stream()
                .map(OrderResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> updateOrder(@PathVariable String id, @Valid @RequestBody OrderRequest orderRequest) {
        logger.debug("Updating order with ID: {}", id);
        Order order = orderService.updateOrder(id, orderRequest);
        return ResponseEntity.ok(new OrderResponse(order));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable String id) {
        logger.debug("Deleting order with ID: {}", id);
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}