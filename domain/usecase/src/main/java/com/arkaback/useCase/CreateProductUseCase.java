package com.arkaback.useCase;

import com.arkaback.entity.Product;
import com.arkaback.exceptions.ProductAlreadyExistsException;
import com.arkaback.ports.output.InventoryPersistencePort;
import com.arkaback.ports.output.ProductPersistencePort;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;

@AllArgsConstructor
public class CreateProductUseCase implements com.arkaback.ports.input.CreateProductUseCase {

    private final ProductPersistencePort productPersistencePort;
    private final InventoryPersistencePort inventoryPersistencePort;

    @Override
    @Transactional
    public Product execute(Product product, Long warehouseId, Long supplierId) {

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
