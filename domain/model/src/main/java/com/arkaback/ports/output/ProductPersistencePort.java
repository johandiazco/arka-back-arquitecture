package com.arkaback.ports.output;

import com.arkaback.entity.Product;

public interface ProductPersistencePort {

    Product save(Product product);
    boolean existsBySku(String sku);

}
