package com.arkaback.ports.input;

import com.arkaback.entity.Inventory;
import com.arkaback.entity.Product;

import java.util.List;

public interface GetLowStockProducts {
    List<Inventory> execute(Integer threshold);
}
