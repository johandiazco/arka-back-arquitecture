package com.arkaback.ports.output;

import com.arkaback.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductPersistencePort {

    Product save(Product product);
    boolean existsBySku(String sku);
    List<Product> findAll();
    Optional<Product> findById(Long id);
    Product updateById(Long id, Product product);
    void deleteById(Long id);



}
