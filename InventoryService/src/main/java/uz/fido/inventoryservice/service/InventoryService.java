package uz.fido.inventoryservice.service;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.fido.inventoryservice.model.Inventory;
import uz.fido.inventoryservice.repository.InventoryRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    public Optional<Inventory> getInventoryById(String id) {
        return inventoryRepository.findById(id);
    }

    public Inventory saveInventory(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    public void deleteInventory(String id) {
        inventoryRepository.deleteById(id);
    }
    public boolean isInStock(String productCode) {
        Optional<Inventory> inventory = inventoryRepository.findByProductCode(productCode);
        return inventory.map(i -> i.getQuantity() > 0).orElse(false);
    }

    public Inventory create(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }
}
