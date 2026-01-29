package com.arkaback.useCase.Product;

import com.arkaback.entity.product.Product;
import com.arkaback.ports.input.Product.GetProductById;
import com.arkaback.ports.output.ProductPersistencePort;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class GetIdProductUseCase implements GetProductById {

    private final ProductPersistencePort productPersistencePort;

    @Override
    public Optional<Product> getById(Long id){
        return productPersistencePort.findById(id);
    }

}
