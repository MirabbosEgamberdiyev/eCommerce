package uz.fido.productservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uz.fido.productservice.model.Product;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
}