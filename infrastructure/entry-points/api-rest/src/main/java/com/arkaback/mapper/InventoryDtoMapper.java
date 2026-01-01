package com.arkaback.mapper;

import com.arkaback.dto.InventoryResponse;
import com.arkaback.entity.Inventory;
import org.springframework.stereotype.Component;

@Component
public class InventoryDtoMapper {

    public InventoryResponse toResponse(Inventory inventory) {
        return InventoryResponse.builder()
                .id(inventory.getId())
                .productId(inventory.getProduct() != null ? inventory.getProduct().getId() : null)
                .productName(inventory.getProduct() != null ? inventory.getProduct().getName() : null)
                .productSku(inventory.getProduct() != null ? inventory.getProduct().getSku() : null)
                .stockActual(inventory.getStockActual())
                .stockReserved(inventory.getStockReserved())
                .stockAvailable(inventory.getStockActual() - inventory.getStockReserved())
                .build();
    }
}
