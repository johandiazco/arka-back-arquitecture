package com.arkaback.ports.output;

import com.arkaback.entity.Inventory;
import com.arkaback.entity.Product;

import java.util.Optional;

public interface InventoryPersistencePort {

    Inventory createInitial(Product product, Long warehouseId, Long supplierId);

    Optional<Inventory> findByProductIdAndWarehouseId(Long productId, Long warehouseId);
    Inventory update(Inventory inventory);

}
