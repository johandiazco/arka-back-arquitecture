package com.arkaback.ports.input;

import com.arkaback.entity.Product;

public interface CreateProduct {

    Product execute(Product product, Long warehouseId, Long supplierId);

}
