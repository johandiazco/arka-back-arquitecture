package com.arkaback.entity.inventory;

import com.arkaback.entity.product.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Getter;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {
    private Long id;
    private Product product;
    private Integer stockActual;
    private Integer stockReserved;
}
