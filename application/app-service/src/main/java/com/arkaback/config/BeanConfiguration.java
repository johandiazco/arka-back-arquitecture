package com.arkaback.config;

import com.arkaback.handler.*;
import com.arkaback.ports.input.*;
import com.arkaback.useCase.Product.*;
import com.arkaback.useCase.Stock.UpdateStockUseCase;
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
    public ListProduct getAllProductsUseCase(ProductPersistencePort productPersistencePort) {
        return new GetAllProductsUseCase(productPersistencePort);
    }

    @Bean
    public GetProductById getProductByIdUseCase(ProductPersistencePort productPersistencePort) {
        return new GetIdProductUseCase(productPersistencePort);
    }

    @Bean
    public UpdateProduct updateProductUseCase(ProductPersistencePort productPersistencePort) {
        return new UpdateProductUseCase(productPersistencePort);
    }

    @Bean
    public DeleteProduct deleteProductUseCase(ProductPersistencePort productPersistencePort) {
        return new DeleteProductUseCase(productPersistencePort);
    }

}
