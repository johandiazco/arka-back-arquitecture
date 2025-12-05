package com.arkaback.ports.input;

import com.arkaback.entity.Product;

public interface CreateProduct {

    Product create(Product product, Long warehouseId, Long supplierId);

}
