package uz.fido.orderservice.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uz.fido.orderservice.model.Order;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
}
