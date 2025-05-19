package uz.fido.orderservice.model;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "orders")
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Id
    private String id;

    @NotBlank(message = "Product ID is required")
    private String productId;

    @Min(value = 1, message = "Quantity must be positive")
    private int quantity;

    @Min(value = 0, message = "Total price cannot be negative")
    private double totalPrice;
}
