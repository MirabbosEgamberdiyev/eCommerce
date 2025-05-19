package uz.fido.inventoryservice.service;


import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uz.fido.inventoryservice.model.Inventory;
import uz.fido.inventoryservice.repository.InventoryRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);

    private final InventoryRepository inventoryRepository;

    public List<Inventory> getAllInventory() {
        logger.debug("Retrieving all inventory");
        return inventoryRepository.findAll();
    }

    public Optional<Inventory> getInventoryById(String id) {
        logger.debug("Retrieving inventory by ID: {}", id);
        return inventoryRepository.findById(id);
    }

    public Inventory createInventory(Inventory inventory) {
        logger.debug("Creating inventory for productCode: {}", inventory.getProductCode());
        if (inventory.getQuantity() < 0) {
            logger.warn("Invalid quantity for productCode: {}", inventory.getProductCode());
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        return inventoryRepository.save(inventory);
    }

    public void deleteInventory(String id) {
        logger.debug("Deleting inventory with ID: {}", id);
        if (!inventoryRepository.existsById(id)) {
            logger.warn("Inventory not found for ID: {}", id);
            throw new IllegalArgumentException("Inventory not found");
        }
        inventoryRepository.deleteById(id);
    }

    public boolean isInStock(String productCode) {
        logger.debug("Checking stock for productCode: {}", productCode);
        Optional<Inventory> inventory = inventoryRepository.findByProductCode(productCode);
        boolean inStock = inventory.map(i -> i.getQuantity() > 0).orElse(false);
        logger.debug("Stock check for productCode {}: {}", productCode, inStock);
        return inStock;
    }
}