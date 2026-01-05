package com.arkaback.useCase.Product;

import com.arkaback.entity.Product;
import com.arkaback.ports.input.Product.UpdateProduct;
import com.arkaback.ports.output.ProductPersistencePort;
import lombok.AllArgsConstructor;
import java.util.Optional;

@AllArgsConstructor
public class UpdateProductUseCase implements UpdateProduct {

    private final ProductPersistencePort persistencePort;

    @Override
    public Optional<Product> update(Long id, Product product) {
        product.validate(); // Validar antes
        return persistencePort.updateById(id, product);
    }
}


