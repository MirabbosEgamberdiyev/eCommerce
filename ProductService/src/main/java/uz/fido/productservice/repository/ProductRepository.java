package uz.fido.productservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uz.fido.productservice.model.Product;

import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByNameContainingIgnoreCase(String name);
}