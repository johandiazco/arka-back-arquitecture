package com.arka.mapper;

import com.arka.dto.ProductCreateRequest;
import com.arka.dto.ProductResponse;
import com.arkaback.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductDtoMapper {

    public Product toDomain(ProductCreateRequest request) {
        return Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .sku(request.getSku())
                .brand(request.getBrand())
                .minStock(request.getMinStock())
                .active(true)
                .category(request.getCategory())
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
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .build();
    }
}
