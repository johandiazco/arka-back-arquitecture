package com.arkaback.handler;

import com.arkaback.entity.Product;
import com.arkaback.ports.input.UpdateProduct;
import com.arkaback.ports.output.ProductPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UpdateProductHandler implements UpdateProduct {

    private final ProductPersistencePort persistencePort;

    @Override
    public Optional<Product> update(Long id, Product product) {
        product.validate();
        return persistencePort.updateById(id, product);
    }
}

