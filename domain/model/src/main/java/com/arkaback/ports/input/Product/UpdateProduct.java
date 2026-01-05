package com.arkaback.ports.input.Product;

import com.arkaback.entity.Product;
import java.util.Optional;

public interface UpdateProduct {
    Optional<Product> update(Long id, Product product);
}

