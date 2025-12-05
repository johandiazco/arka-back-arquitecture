package com.arkaback.useCase.Product;

import com.arkaback.entity.Product;
import com.arkaback.exceptions.ProductAlreadyExistsException;
import com.arkaback.ports.input.CreateProduct;
import com.arkaback.ports.output.InventoryPersistencePort;
import com.arkaback.ports.output.ProductPersistencePort;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CreateProductUseCase implements CreateProduct {

    private final ProductPersistencePort productPersistencePort;
    private final InventoryPersistencePort inventoryPersistencePort;

    @Override
    public Product create(Product product, Long warehouseId, Long supplierId) {

        product.validate();

        // Validar SKU duplicado ANTES de guardar
        if (productPersistencePort.existsBySku(product.getSku())) {
            throw new ProductAlreadyExistsException(
                    "El SKU " + product.getSku() + " ya existe"
            );
        }

        // Guardar producto
        Product savedProduct = productPersistencePort.save(product);

        // Crear inventario inicial
        inventoryPersistencePort.createInitial(savedProduct, warehouseId, supplierId);

        return savedProduct;

    }
}
