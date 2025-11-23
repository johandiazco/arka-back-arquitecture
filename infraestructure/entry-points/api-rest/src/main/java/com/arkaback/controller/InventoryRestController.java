package com.arkaback.controller;

import com.arkaback.dto.InventoryResponse;
import com.arkaback.dto.StockUpdateRequest;
import com.arkaback.entity.Inventory;
import com.arkaback.mapper.InventoryDtoMapper;
import com.arkaback.ports.in.UpdateStockUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
public class InventoryRestController {

    private final UpdateStockUseCase updateStockUseCase;
    private final InventoryDtoMapper mapper;

    public InventoryRestController(UpdateStockUseCase updateStockUseCase,
                                   InventoryDtoMapper mapper) {
        this.updateStockUseCase = updateStockUseCase;
        this.mapper = mapper;
    }

    @PutMapping("/stock/{productId}/{warehouseId}")
    public ResponseEntity<InventoryResponse> updateStock(
            @PathVariable Long productId,
            @PathVariable Long warehouseId,
            @Valid @RequestBody StockUpdateRequest request) {

        Inventory updated = updateStockUseCase.execute(productId, warehouseId, request.getNewStock());
        InventoryResponse response = mapper.toResponse(updated);
        return ResponseEntity.ok(response);
    }
}
