package uz.fido.inventoryservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uz.fido.inventoryservice.model.Inventory;
import uz.fido.inventoryservice.repository.InventoryRepository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class InventoryServiceApplicationTests {

	@Autowired
	private InventoryRepository inventoryRepository;

	@Test
	public void contextLoads() {
		Inventory inventory = new Inventory();
		inventory.setQuantity(10);
		inventory.setQuantity(1000);

		inventoryRepository.save(inventory);

		assertThat(inventoryRepository.count()).isGreaterThan(0);
	}
}
