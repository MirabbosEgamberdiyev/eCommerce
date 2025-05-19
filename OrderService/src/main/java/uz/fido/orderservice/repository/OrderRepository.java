package uz.fido.orderservice.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import uz.fido.orderservice.model.Order;

import java.util.List;

public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByProductId(String productId);
}