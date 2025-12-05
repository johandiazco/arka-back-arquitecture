package com.arkaback.handler;

import com.arkaback.ports.input.DeleteProduct;
import com.arkaback.ports.output.ProductPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteProductHandler implements DeleteProduct {

    private final ProductPersistencePort persistencePort;

    @Override
    public void delete(Long id) {
        persistencePort.deleteById(id);
    }
}

