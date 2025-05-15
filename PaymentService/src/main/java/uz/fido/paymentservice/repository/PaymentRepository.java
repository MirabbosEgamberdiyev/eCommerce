package uz.fido.paymentservice.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import uz.fido.paymentservice.model.Payment;

import java.util.Optional;

public interface PaymentRepository extends MongoRepository<Payment, String> {
    Optional<Payment> findByOrderId(String orderId);
}
