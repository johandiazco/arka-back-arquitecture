package com.arkaback.ports.input;

import com.arkaback.entity.Inventory;

public interface UpdateStock {
    Inventory execute(Long productId, Long warehouseId, Integer newStock);
}
