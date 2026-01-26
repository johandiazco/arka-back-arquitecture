package com.arkaback.controller;

import com.arkaback.dto.Inventory.InventoryResponse;
import com.arkaback.dto.Stock.StockUpdateRequest;
import com.arkaback.entity.Inventory;
import com.arkaback.mapper.InventoryDtoMapper;
import com.arkaback.ports.input.Product.UpdateStock;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.arkaback.ports.input.Product.GetLowStockProduct;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/inventory")
public class InventoryRestController {

    private final UpdateStock updateStockUseCase;
    private final InventoryDtoMapper mapper;
    private final GetLowStockProduct getLowStockProduct;

    //Actualizar stock
    @PutMapping("/stock/{productId}/{warehouseId}")
    public ResponseEntity<InventoryResponse> updateStock(
            @PathVariable(name = "productId") Long productId,
            @PathVariable(name = "warehouseId") Long warehouseId,
            @Valid @RequestBody StockUpdateRequest request) {

        Inventory updated = updateStockUseCase.execute(productId, warehouseId, request.getNewStock());
        InventoryResponse response = mapper.toResponse(updated);
        return ResponseEntity.ok(response);
    }

    //Stock bajo
    @GetMapping("/low-stock")
    public ResponseEntity<List<InventoryResponse>> getLowStock(
            @RequestParam(defaultValue = "10") Integer threshold) {

        List<Inventory> inventories = getLowStockProduct.execute(threshold);
        return ResponseEntity.ok(inventories.stream().map(mapper::toResponse).toList());
    }

}
