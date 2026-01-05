package com.arkaback.ports.input.Product;

import com.arkaback.entity.Inventory;

import java.util.List;

public interface GetLowStockProduct {
    List<Inventory> execute(Integer threshold);
}
