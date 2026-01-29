package com.arkaback.ports.output;

import com.arkaback.entity.inventory.Inventory;
import com.arkaback.entity.product.Product;

import java.util.List;
import java.util.Optional;

public interface InventoryPersistencePort {

    Inventory createInitial(Product product, Long warehouseId, Long supplierId);

    Optional<Inventory> findByProductIdAndWarehouseId(Long productId, Long warehouseId);
    Inventory update(Inventory inventory);
    List<Inventory> findByStockActualLessThan(Integer threshold);

}
