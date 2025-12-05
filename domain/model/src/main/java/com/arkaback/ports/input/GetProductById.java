package com.arkaback.ports.input;

import com.arkaback.entity.Product;

import java.util.Optional;

public interface GetProductById {
    Optional<Product> getById(Long id);
}
