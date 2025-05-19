package uz.fido.productservice.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "inventory")
public class Inventory {
    @Id
    private String id;

    @NotBlank(message = "Product code is required")
    private String productCode;

    @Min(value = 0, message = "Quantity cannot be negative")
    private int quantity;
}