package uz.fido.orderservice.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.fido.orderservice.model.Order;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private String id;
    private String productId;
    private int quantity;
    private double totalPrice;

    public OrderResponse(Order order) {
        this.id = order.getId();
        this.productId = order.getProductId();
        this.quantity = order.getQuantity();
        this.totalPrice = order.getTotalPrice();
    }
}