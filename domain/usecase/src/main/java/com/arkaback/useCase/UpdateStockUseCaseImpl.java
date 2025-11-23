package com.arkaback.useCase;

import com.arkaback.entity.Inventory;
import com.arkaback.exceptions.InvalidStockException;
import com.arkaback.exceptions.InventoryNotFoundException;
import com.arkaback.ports.in.UpdateStockUseCase;
import com.arkaback.ports.out.InventoryPersistencePort;

public class UpdateStockUseCaseImpl implements UpdateStockUseCase {
    private final InventoryPersistencePort inventoryPersistencePort;

    public UpdateStockUseCaseImpl(InventoryPersistencePort inventoryPersistencePort) {
        this.inventoryPersistencePort = inventoryPersistencePort;
    }

    @Override
    public Inventory execute(Long productId, Long warehouseId, Integer newStock) {
        // 1. Validar que el stock no sea negativo
        if (newStock < 0) {
            throw new InvalidStockException("El stock no puede ser negativo");
        }

        // 2. Buscar el inventory
        Inventory inventory = inventoryPersistencePort
                .findByProductIdAndWarehouseId(productId, warehouseId)
                .orElseThrow(() -> new InventoryNotFoundException(
                        "Inventario no encontrado para producto " + productId + " en bodega " + warehouseId
                ));

        // 3. Validar que el nuevo stock no sea menor al reservado
        if (newStock < inventory.getStockReserved()) {
            throw new InvalidStockException(
                    "El stock no puede ser menor al stock reservado (" + inventory.getStockReserved() + ")"
            );
        }

        // 4. Actualizar el stock (crear nuevo objeto porque Inventory es inmutable)
        Inventory updatedInventory = Inventory.builder()
                .id(inventory.getId())
                .product(inventory.getProduct())
                .stockActual(newStock)
                .stockReserved(inventory.getStockReserved())
                .build();

        // 5. Guardar y retornar
        return inventoryPersistencePort.update(updatedInventory);
    }
}
