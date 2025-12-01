package com.arkaback.controller;

import com.arkaback.dto.InventoryResponse;
import com.arkaback.dto.StockUpdateRequest;
import com.arkaback.entity.Inventory;
import com.arkaback.mapper.InventoryDtoMapper;
import com.arkaback.ports.input.UpdateStockUseCase;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/inventory")
public class InventoryRestController {

    private final UpdateStockUseCase updateStockUseCase;
    private final InventoryDtoMapper mapper;

    @PutMapping("/stock/{productId}/{warehouseId}")
    public ResponseEntity<InventoryResponse> updateStock(
            @PathVariable(name = "productId") Long productId,
            @PathVariable(name = "warehouseId") Long warehouseId,
            @Valid @RequestBody StockUpdateRequest request) {

        Inventory updated = updateStockUseCase.execute(productId, warehouseId, request.getNewStock());
        InventoryResponse response = mapper.toResponse(updated);
        return ResponseEntity.ok(response);
    }
}
