package uz.fido.inventoryservice.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import uz.fido.inventoryservice.model.Inventory;

public interface InventoryRepository extends MongoRepository<Inventory, String> {
}
