package com.arkaback.mappers;

import com.arkaback.entity.Categorie;
import com.arkaback.entity.Inventory;
import com.arkaback.entity.InventoryEntity;
import com.arkaback.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class InventoryPersistenceMapper {

    public Inventory toDomain(InventoryEntity entity) {
        Product product = null;
        if (entity.getProduct() != null) {
            Categorie category = null;
            if (entity.getProduct().getCategory() != null) {
                category = Categorie.builder()
                        .id(entity.getProduct().getCategory().getId())
                        .name(entity.getProduct().getCategory().getName())
                        .description(entity.getProduct().getCategory().getDescription())
                        .build();
            }

            product = Product.builder()
                    .id(entity.getProduct().getId())
                    .name(entity.getProduct().getName())
                    .sku(entity.getProduct().getSku())
                    .price(entity.getProduct().getPrice())
                    .brand(entity.getProduct().getBrand())
                    .minStock(entity.getProduct().getMinStock())
                    .isActive(entity.getProduct().getIsActive())
                    .category(category)
                    .build();
        }

        return Inventory.builder()
                .id(entity.getId())
                .product(product)
                .stockActual(entity.getStockActual())
                .stockReserved(entity.getStockReserved())
                .build();
    }

    public InventoryEntity toEntity(Inventory inventory) {
        return InventoryEntity.builder()
                .id(inventory.getId())
                .stockActual(inventory.getStockActual())
                .stockReserved(inventory.getStockReserved())
                .build();
    }
}
