package com.arkaback.useCase.Product;

import com.arkaback.entity.Product;
import com.arkaback.exceptions.ProductAlreadyExistsException;
import com.arkaback.ports.input.CreateProduct;
import com.arkaback.ports.output.InventoryPersistencePort;
import com.arkaback.ports.output.ProductPersistencePort;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;

@AllArgsConstructor
public class CreateProductUseCase implements CreateProduct {

    private final ProductPersistencePort productPersistencePort;
    private final InventoryPersistencePort inventoryPersistencePort;

    @Override
    @Transactional
    public Product create(Product product, Long warehouseId, Long supplierId) {

        product.validate();

        try {
            Product savedProduct = productPersistencePort.save(product);
            // Creamos inventory inicial
            inventoryPersistencePort.createInitial(savedProduct, warehouseId, supplierId);
            return savedProduct;
        } catch (DataIntegrityViolationException ex) {
            // Manejo de constraint
            throw new ProductAlreadyExistsException("El SKU " + product.getSku() + " ya existe");
        }

    }
}
