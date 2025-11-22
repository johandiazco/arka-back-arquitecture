package com.arkaback.adapter;

import com.arkaback.entity.Inventory;
import com.arkaback.entity.InventoryEntity;
import com.arkaback.entity.Product;
import com.arkaback.entity.ProductEntity;
import com.arkaback.ports.out.InventoryPersistencePort;
import com.arkaback.repository.InventoryJpaRepository;
import org.springframework.stereotype.Component;

@Component
public class InventoryPersistenceAdapter implements InventoryPersistencePort {

    private final InventoryJpaRepository inventoryRepository;

    public InventoryPersistenceAdapter(InventoryJpaRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public Inventory createInitial(Product product, Long warehouseId, Long supplierId) {
        ProductEntity productEntity = ProductEntity.builder()
                .id(product.getId())
                .build();

        InventoryEntity inventory = InventoryEntity.builder()
                .product(productEntity)
                .stockActual(0)
                .stockReserved(0)
                .build();

        InventoryEntity saved = inventoryRepository.save(inventory);

        return Inventory.builder()
                .id(saved.getId())
                .product(product)
                .stockActual(saved.getStockActual())
                .stockReserved(saved.getStockReserved())
                .build();
    }
}
