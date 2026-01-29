package com.arkaback.useCase.Product;


import com.arkaback.entity.product.Product;
import com.arkaback.ports.input.Product.ListProduct;
import com.arkaback.ports.output.ProductPersistencePort;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class GetAllProductsUseCase implements ListProduct {

    private final ProductPersistencePort productPersistencePort;

    @Override
    public List<Product> getAll() {
        return productPersistencePort.findAll();
    }

}
