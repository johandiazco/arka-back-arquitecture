package com.arkaback.ports.out;

import com.arkaback.entity.Inventory;
import com.arkaback.entity.Product;

public interface InventoryPersistencePort {

    Inventory createInitial(Product product, Long warehouseId, Long supplierId);

}
