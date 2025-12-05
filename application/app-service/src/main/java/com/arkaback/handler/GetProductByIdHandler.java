package com.arkaback.handler;

import com.arkaback.entity.Product;
import com.arkaback.ports.input.GetProductById;
import com.arkaback.ports.output.ProductPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetProductByIdHandler implements GetProductById {

    private final ProductPersistencePort persistencePort;

    @Override
    public Optional<Product> getById(Long id) {
        return persistencePort.findById(id);
    }
}

