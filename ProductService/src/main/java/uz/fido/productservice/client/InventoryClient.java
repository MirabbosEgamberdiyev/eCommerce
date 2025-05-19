package uz.fido.productservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import uz.fido.productservice.model.Inventory;

@FeignClient(name = "INVENTORY-SERVICE")
public interface InventoryClient {
    @PostMapping("/api/inventory")
    Inventory createInventory(@RequestBody Inventory inventory);
}