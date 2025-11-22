package com.arkaback.repository;

import com.arkaback.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {
    boolean existsBySku(String sku);
}
