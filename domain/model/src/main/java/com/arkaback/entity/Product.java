package com.arkaback.entity;

import com.arkaback.exceptions.InvalidPriceException;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String sku;
    private String brand;
    private Integer minStock;
    private Boolean active;
    private Categorie category;

    public void validatePrice() {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPriceException("El precio debe ser mayor a 0");
        }
    }
}
