package com.arkaback.handler;

import com.arkaback.entity.Product;
import com.arkaback.dto.ProductUpdateRequest;
import com.arkaback.mappers.ProductPersistenceMapper;
import com.arkaback.ports.output.ProductPersistencePort;
import com.arkaback.useCase.Product.UpdateProductUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UpdateProductHandler implements UpdateProductUseCase {

    private final ProductPersistencePort persistencePort;
    private final ProductPersistenceMapper persistenceMapper;

    @Override
    public Optional<Product> update(Long id, ProductUpdateRequest request) {
        Optional<Product> optional = persistencePort.findById(id);
        if (optional.isEmpty()) return Optional.empty();

        Product product = optional.get();
        // apply partial update in mapper
        persistenceMapper.updateDomainFromRequest(request, product);
        // persist changes
        return persistencePort.updateById(id, product);
    }
}

