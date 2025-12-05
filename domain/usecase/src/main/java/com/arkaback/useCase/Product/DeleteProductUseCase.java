package com.arkaback.useCase.Product;

import com.arkaback.ports.input.DeleteProduct;
import com.arkaback.ports.output.ProductPersistencePort;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DeleteProductUseCase implements DeleteProduct {

    private final ProductPersistencePort persistencePort;

    @Override
    public void delete(Long id) {

        persistencePort.findById(id)
             .ifPresent(product -> {
                 product.setIsActive(false);
                 persistencePort.save(product);
             });
    }
}
