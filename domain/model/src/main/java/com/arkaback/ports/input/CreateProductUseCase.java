package com.arkaback.ports.input;

import com.arkaback.entity.Product;

public interface CreateProductUseCase {

    Product execute(Product product, Long warehouseId, Long supplierId);

}
