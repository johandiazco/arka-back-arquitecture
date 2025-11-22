package com.arkaback.adapter;

import com.arkaback.entity.Product;
import com.arkaback.entity.ProductEntity;
import com.arkaback.mappers.ProductPersistenceMapper;
import com.arkaback.ports.out.ProductPersistencePort;
import com.arkaback.repository.ProductJpaRepository;
import org.springframework.stereotype.Component;

@Component
public class ProductPersistenceAdapter implements ProductPersistencePort {

    private final ProductJpaRepository jpaRepository;
    private final ProductPersistenceMapper mapper;

    public ProductPersistenceAdapter(ProductJpaRepository jpaRepository,
                                     ProductPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Product save(Product product) {
        ProductEntity entity = mapper.toEntity(product);
        ProductEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public boolean existsBySku(String sku) {
        return jpaRepository.existsBySku(sku);
    }
}
