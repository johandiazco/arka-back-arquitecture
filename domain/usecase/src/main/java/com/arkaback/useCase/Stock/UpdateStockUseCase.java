package com.arkaback.useCase.Stock;

import com.arkaback.entity.inventory.Inventory;
import com.arkaback.exceptions.domain.InvalidStockException;
import com.arkaback.exceptions.infrastructure.InventoryNotFoundException;
import com.arkaback.ports.input.Product.UpdateStock;
import com.arkaback.ports.output.InventoryPersistencePort;

public class UpdateStockUseCase implements UpdateStock {
    private final InventoryPersistencePort inventoryPersistencePort;

    public UpdateStockUseCase(InventoryPersistencePort inventoryPersistencePort) {
        this.inventoryPersistencePort = inventoryPersistencePort;
    }

    @Override
    public Inventory execute(Long productId, Long warehouseId, Integer newStock) {
        // se Valida que el stock no sea negativo
        if (newStock < 0) {
            throw new InvalidStockException("El stock no puede ser negativo");
        }

        // Buscamos el inventory
        Inventory inventory = inventoryPersistencePort
                .findByProductIdAndWarehouseId(productId, warehouseId)
                .orElseThrow(() -> new InventoryNotFoundException(
                        "Inventario no encontrado para producto " + productId + " en bodega " + warehouseId
                ));

        // Validamos que el nuevo stock no sea menor al reservado
        if (newStock < inventory.getStockReserved()) {
            throw new InvalidStockException(
                    "El stock no puede ser menor al stock reservado (" + inventory.getStockReserved() + ")"
            );
        }

        // Actualizamos el stock (crear nuevo objeto porque Inventory es inmutable)
        Inventory updatedInventory = Inventory.builder()
                .id(inventory.getId())
                .product(inventory.getProduct())
                .stockActual(newStock)
                .stockReserved(inventory.getStockReserved())
                .build();

        // Guarda y retorna
        return inventoryPersistencePort.update(updatedInventory);
    }
}
