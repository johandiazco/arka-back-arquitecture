package com.arkaback.ports.input;

import com.arkaback.entity.Product;
import com.arkaback.dto.ProductUpdateRequest;
import java.util.Optional;

public interface UpdateProduct {
    Optional<Product> update(Long id, ProductUpdateRequest request);
}

