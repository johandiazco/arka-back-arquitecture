package com.arkaback.handler;

import com.arkaback.entity.Product;
import com.arkaback.ports.input.CreateProduct;
import com.arkaback.ports.output.ProductPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateProductHandler implements CreateProduct {

    private final ProductPersistencePort persistencePort;

    @Override
    public Product create(Product product, Long warehouseId, Long supplierId) {
        return persistencePort.save(product);
    }
}

