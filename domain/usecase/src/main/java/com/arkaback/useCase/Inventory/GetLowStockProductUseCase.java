package com.arkaback.useCase.Product;

import com.arkaback.entity.Inventory;
import com.arkaback.ports.input.GetLowStockProduct;
import com.arkaback.ports.output.InventoryPersistencePort;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class GetLowStockProductUseCase implements GetLowStockProduct {

    private final InventoryPersistencePort persistencePort;

    @Override
    public List<Inventory> execute(Integer threshold){
        return persistencePort.findByStockActualLessThan(threshold);
    }

}
