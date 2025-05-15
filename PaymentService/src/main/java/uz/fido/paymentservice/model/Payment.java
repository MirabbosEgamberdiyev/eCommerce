package uz.fido.paymentservice.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "payments")
public class Payment {
    @Id
    private String id;

    private String orderId;
    private Double amount;
    private String paymentMethod; // e.g. "CREDIT_CARD", "PAYPAL", "CASH"
    private String status; // e.g. "PENDING", "COMPLETED", "FAILED"

    private LocalDateTime paymentDate;
}
