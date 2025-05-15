package uz.fido.inventoryservice.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import uz.fido.inventoryservice.model.Inventory;

import java.util.Optional;

public interface InventoryRepository extends MongoRepository<Inventory, String> {
    Optional<Inventory> findByProductCode(String productCode);

}
