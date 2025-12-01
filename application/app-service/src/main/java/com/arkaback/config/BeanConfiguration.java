package com.arkaback.config;

import com.arkaback.ports.input.CreateProduct;
import com.arkaback.ports.input.UpdateStock;
import com.arkaback.useCase.Product.CreateProductUseCase;
import com.arkaback.useCase.Product.GetAllProductsUseCase;
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
    public CreateProduct createProductUseCase(
            ProductPersistencePort productPersistencePort,
            InventoryPersistencePort inventoryPersistencePort) {
        return new CreateProductUseCase(productPersistencePort, inventoryPersistencePort);
    }

    @Bean
    public UpdateStock updateStockUseCase(InventoryPersistencePort inventoryPersistencePort) {
        return new UpdateStockUseCase(inventoryPersistencePort);
    }

    @Bean
    public GetAllProductsUseCase getAllProductsUseCase(ProductPersistencePort port)

}
