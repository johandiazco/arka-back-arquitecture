package com.arkaback.mappers;

import com.arkaback.entity.Category;
import com.arkaback.entity.CategoryEntity;
import com.arkaback.entity.Product;
import com.arkaback.entity.ProductEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductPersistenceMapper {

    public ProductEntity toEntity(Product product) {
        CategoryEntity categoryEntity = null;
        if (product.getCategory() != null) {
            categoryEntity = CategoryEntity.builder()
                    .id(product.getCategory().getId())
                    .name(product.getCategory().getName())
                    .description(product.getCategory().getDescription())
                    .build();
        }

        return ProductEntity.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .sku(product.getSku())
                .brand(product.getBrand())
                .minStock(product.getMinStock())
                .isActive(product.getIsActive())
                .category(categoryEntity)
                .build();
    }

    public Product toDomain(ProductEntity entity) {
        if (entity == null) return null;
        Category category = null;
        if (entity.getCategory() != null) {
            category = Category.builder()
                    .id(entity.getCategory().getId())
                    .name(entity.getCategory().getName())
                    .description(entity.getCategory().getDescription())
                    .build();
        }

        return Product.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .sku(entity.getSku())
                .brand(entity.getBrand())
                .minStock(entity.getMinStock())
                .isActive(entity.getIsActive())
                .category(category)
                .build();
    }
}
