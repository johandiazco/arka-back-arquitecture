package com.arkaback.ports.input.Product;

import com.arkaback.entity.product.Product;

public interface CreateProduct {

    Product create(Product product, Long warehouseId, Long supplierId);

}
