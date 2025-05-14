package uz.fido.orderservice.dto;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {

    @NotNull
    private String productId;

    @NotNull
    private Integer quantity;

    @NotNull
    private Double totalPrice;
}
