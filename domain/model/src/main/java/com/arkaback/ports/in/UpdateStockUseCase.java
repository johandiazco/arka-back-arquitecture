package com.arkaback.ports.in;

import com.arkaback.entity.Inventory;

public interface UpdateStockUseCase {
    Inventory execute(Long productId, Long warehouseId, Integer newStock);
}
