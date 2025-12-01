package com.arkaback.ports.output;

import com.arkaback.entity.Product;

import java.util.List;

public interface ProductPersistencePort {

    Product save(Product product);
    boolean existsBySku(String sku);
    List<Product> findAll();


}
