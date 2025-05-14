package uz.fido.orderservice.model;


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
    private String productId;
    private Integer quantity;
    private Double totalPrice;

}
