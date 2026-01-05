package com.arkaback.ports.input.Product;

import com.arkaback.entity.Inventory;

public interface UpdateStock {
    Inventory execute(Long productId, Long warehouseId, Integer newStock);
}
