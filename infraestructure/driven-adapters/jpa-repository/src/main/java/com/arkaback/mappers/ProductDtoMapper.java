package com.arkaback.mappers;

import com.arkaback.entity.Product;
import com.arkaback.dto.ProductCreateRequest;
import com.arkaback.dto.ProductResponse;
import org.springframework.stereotype.Component;

@Component
public class ProductDtoMapper {

    public Product toDomain(ProductCreateRequest req) {
        return Product.builder()
                .name(req.getName())
                .description(req.getDescription())
                .price(req.getPrice())
                .sku(req.getSku())
                .brand(req.getBrand())
                .minStock(req.getMinStock())
                .isActive(true)
                .build();
    }

    public ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .sku(product.getSku())
                .brand(product.getBrand())
                .minStock(product.getMinStock())
                .isActive(product.getIsActive())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .build();
    }
}

