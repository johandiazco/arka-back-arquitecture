package com.arkaback.dto.Inventory;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String productSku;
    private Integer stockActual;
    private Integer stockReserved;
    private Integer stockAvailable;
}
