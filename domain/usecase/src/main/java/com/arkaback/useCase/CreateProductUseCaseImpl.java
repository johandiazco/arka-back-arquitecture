package com.arkaback.useCase;

import com.arkaback.entity.Product;
import com.arkaback.exceptions.ProductAlreadyExistsException;
import com.arkaback.ports.in.CreateProductUseCase;
import com.arkaback.ports.out.InventoryPersistencePort;
import com.arkaback.ports.out.ProductPersistencePort;

public class CreateProductUseCaseImpl implements CreateProductUseCase {

    private final ProductPersistencePort productPersistencePort;
    private final InventoryPersistencePort inventoryPersistencePort;

    public CreateProductUseCaseImpl(
            ProductPersistencePort productPersistencePort,
            InventoryPersistencePort inventoryPersistencePort) {
        this.productPersistencePort = productPersistencePort;
        this.inventoryPersistencePort = inventoryPersistencePort;
    }

    @Override
    public Product execute(Product product, Long warehouseId, Long supplierId) {
        // Validamos SKU duplicado
        if (productPersistencePort.existsBySku(product.getSku())) {
            throw new ProductAlreadyExistsException("El SKU " + product.getSku() + " ya existe");
        }

        // Validamos precio
        product.validate();

        // se Guarda producto
        Product savedProduct = productPersistencePort.save(product);

        // Creamos inventory inicial
        inventoryPersistencePort.createInitial(savedProduct, warehouseId, supplierId);

        return savedProduct;
    }

}
