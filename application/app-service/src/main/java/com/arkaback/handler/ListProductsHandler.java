package com.arkaback.handler;

import com.arkaback.entity.Product;
import com.arkaback.ports.input.ListProduct;
import com.arkaback.ports.output.ProductPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ListProductsHandler implements ListProduct {

    private final ProductPersistencePort persistencePort;

    @Override
    public List<Product> getAll() {
        return persistencePort.findAll();
    }
}

