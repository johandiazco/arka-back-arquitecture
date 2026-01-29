package com.arkaback.ports.input.Product;

import com.arkaback.entity.inventory.Inventory;

import java.util.List;

public interface GetLowStockProduct {
    List<Inventory> execute(Integer threshold);
}
