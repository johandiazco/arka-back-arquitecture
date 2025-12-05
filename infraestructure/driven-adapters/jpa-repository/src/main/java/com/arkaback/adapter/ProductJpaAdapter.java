package com.arkaback.adapter;

import com.arkaback.entity.Product;
import com.arkaback.entity.ProductEntity;
import com.arkaback.mappers.ProductPersistenceMapper;
import com.arkaback.ports.output.ProductPersistencePort;
import com.arkaback.repository.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductJpaAdapter implements ProductPersistencePort{

    private final ProductJpaRepository productJpaRepository;
    private final ProductPersistenceMapper productEntityMapper;

    @Override
    public Product save(Product product) {
        ProductEntity entity = productEntityMapper.toEntity(product);
        ProductEntity saved = productJpaRepository.save(entity);
        return productEntityMapper.toDomain(saved);
    }

    @Override
    public boolean existsBySku(String sku) {
        return productJpaRepository.existsBySku(sku);
    }

    @Override
    public List<Product> findAll() {
        return productJpaRepository.findAll()
                .stream()
                .map(productEntityMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productJpaRepository.findById(id)
                .map(productEntityMapper::toDomain);
    }

    @Override
    public Optional<Product> updateById(Long id, Product product) {
        return productJpaRepository.findById(id)
                .map(entity -> {
                    ProductEntity updated = productEntityMapper.updateEntityFromDomain(product, entity);
                    ProductEntity saved = productJpaRepository.save(updated);
                    return productEntityMapper.toDomain(saved);
                });
    }

    @Override
    public void deleteById(Long id) {
        productJpaRepository.deleteById(id);
    }


}