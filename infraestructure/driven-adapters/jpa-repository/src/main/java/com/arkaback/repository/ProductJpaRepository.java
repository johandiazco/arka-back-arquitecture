package com.arkaback.repository;

import com.arkaback.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {
    boolean existsBySku(String sku);
    Optional<ProductEntity> findById(Long id);
    List<ProductEntity> findAll();
    List<ProductEntity> findByCategory_Id(Long categoryId);
    List<ProductEntity> findByBrandContainingIgnoreCase(String brand);
}
