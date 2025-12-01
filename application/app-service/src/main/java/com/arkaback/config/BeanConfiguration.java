package com.arkaback.config;

import com.arkaback.useCase.CreateProductUseCase;
import com.arkaback.useCase.UpdateStockUseCase;
import com.arkaback.ports.output.InventoryPersistencePort;
import com.arkaback.ports.output.ProductPersistencePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.arkaback")
public class BeanConfiguration {

    @Bean
    public com.arkaback.ports.input.CreateProductUseCase createProductUseCase(
            ProductPersistencePort productPersistencePort,
            InventoryPersistencePort inventoryPersistencePort) {
        return new CreateProductUseCase(productPersistencePort, inventoryPersistencePort);
    }

    @Bean
    public com.arkaback.ports.input.UpdateStockUseCase updateStockUseCase(InventoryPersistencePort inventoryPersistencePort) {
        return new UpdateStockUseCase(inventoryPersistencePort);
    }
}
