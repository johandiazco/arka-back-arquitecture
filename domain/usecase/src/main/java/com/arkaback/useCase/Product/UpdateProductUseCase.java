package com.arkaback.useCase.Product;

import com.arkaback.entity.Product;
import com.arkaback.ports.input.UpdateProduct;
import com.arkaback.ports.output.ProductPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UpdateProductUseCase implements UpdateProduct {

    private final ProductPersistencePort persistencePort;
    private final ProductPersistenceMapper mapper;

    @Override
    public Optional<Product> update(Long id, ProductUpdateRequest request) {

        Optional<Product> optional = persistencePort.findById(id);
        if (optional.isEmpty()) {
            return Optional.empty();
        }

        Product product = optional.get();

        mapper.updateDomainFromRequest(request, product);

        return persistencePort.updateById(id, product);
    }
}


