package com.arkaback.adapter;

import com.arkaback.entity.*;
import com.arkaback.mappers.InventoryPersistenceMapper;
import com.arkaback.ports.output.InventoryPersistencePort;
import com.arkaback.repository.InventoryJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class InventoryPersistenceAdapter implements InventoryPersistencePort {

    private final InventoryJpaRepository inventoryRepository;
    private final InventoryPersistenceMapper mapper;

    public InventoryPersistenceAdapter(InventoryJpaRepository inventoryRepository,
                                       InventoryPersistenceMapper mapper) {
        this.inventoryRepository = inventoryRepository;
        this.mapper = mapper;
    }

    @Override
    public Inventory createInitial(Product product, Long warehouseId, Long supplierId) {
        ProductEntity productEntity = ProductEntity.builder()
                .id(product.getId())
                .build();

        WarehouseEntity warehouseEntity = WarehouseEntity.builder()
                .id(warehouseId)
                .build();

        SupplierEntity supplierEntity = SupplierEntity.builder()
                .id(supplierId)
                .build();

        InventoryEntity inventory = InventoryEntity.builder()
                .product(productEntity)
                .warehouse(warehouseEntity)
                .supplier(supplierEntity)
                .stockActual(0)
                .stockReserved(0)
                .build();

        InventoryEntity saved = inventoryRepository.save(inventory);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Inventory> findByProductIdAndWarehouseId(Long productId, Long warehouseId) {
        return inventoryRepository.findByProduct_IdAndWarehouse_Id(productId, warehouseId)
                .map(mapper::toDomain);
    }

    @Override
    public Inventory update(Inventory inventory) {
        // Buscar la entidad existente
        InventoryEntity existingEntity = inventoryRepository.findById(inventory.getId())
                .orElseThrow(() -> new RuntimeException("Inventory no encontrado"));

        // Actualizar campos necesarios
        existingEntity.setStockActual(inventory.getStockActual());
        existingEntity.setStockReserved(inventory.getStockReserved());

        // Guardamos
        InventoryEntity saved = inventoryRepository.save(existingEntity);

        // Retornar como dominio
        return mapper.toDomain(saved);
    }
}
