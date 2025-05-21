package uz.fido.productservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @Min(value = 0, message = "Price cannot be negative")
    private double price;

    private String userEmail;
}