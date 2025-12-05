package com.arkaback.useCase.Product;

import com.arkaback.entity.Product;
import com.arkaback.ports.input.GetProductById;
import com.arkaback.ports.output.ProductPersistencePort;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class GetIdProductUseCase implements GetProductById {

    private ProductPersistencePort productPersistencePort;

    @Override
    public Optional<Product> getById(Long id){
        return productPersistencePort.findById(id);
    }

}
