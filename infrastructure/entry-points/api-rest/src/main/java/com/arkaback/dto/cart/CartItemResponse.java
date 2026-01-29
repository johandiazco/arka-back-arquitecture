package com.arkaback.dto.cart;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {

    private Long id;

    //Informacion del producto
    private Long productId;
    private String productName;
    private String productSku;
    private String productBrand;

    //Detalle del producto u item
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;

    private LocalDateTime addedAt;
    private LocalDateTime updatedAt;
}
