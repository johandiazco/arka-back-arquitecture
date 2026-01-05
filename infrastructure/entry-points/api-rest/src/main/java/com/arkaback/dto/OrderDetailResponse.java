package com.arkaback.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailResponse {

    private Long id;

    // Info product
    private Long productId;
    private String productName;
    private String productSku;

    // Detalle línea
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subTotal;
}
