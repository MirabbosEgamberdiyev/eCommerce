package uz.fido.paymentservice.model;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "payments")
public class Payment {
    @Id
    private String id;

    @NotBlank(message = "Order ID is required")
    @Indexed(unique = true)
    private String orderId;

    @Min(value = 0, message = "Amount cannot be negative")
    private Double amount;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod; // e.g., CREDIT_CARD, PAYPAL, CASH

    @NotBlank(message = "Status is required")
    private String status; // e.g., PENDING, COMPLETED, FAILED

    private LocalDateTime paymentDate;
}
