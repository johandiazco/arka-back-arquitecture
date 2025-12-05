package com.arkaback.config;

import com.arkaback.handler.*;
import com.arkaback.mapper.ProductDtoMapper;
import com.arkaback.mappers.ProductPersistenceMapper;
import com.arkaback.ports.input.CreateProduct;
import com.arkaback.ports.input.UpdateStock;
import com.arkaback.useCase.Product.CreateProductUseCase;
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
    public CreateProduct createProduct(
            ProductPersistencePort productPersistencePort,
            InventoryPersistencePort inventoryPersistencePort) {
        return new CreateProductUseCase(productPersistencePort, inventoryPersistencePort);
    }

    @Bean
    public UpdateStock updateStock(InventoryPersistencePort inventoryPersistencePort) {
        return new UpdateStockUseCase(inventoryPersistencePort);
    }

    @Bean
    public ProductDtoMapper productDtoMapper() {
        return new ProductDtoMapper();
    }

    @Bean
    public ProductPersistenceMapper productPersistenceMapper() {
        return new ProductPersistenceMapper();
    }

    // Use case beans (if your use case classes are not component-scanned)
    @Bean
    public ListProductsHandler listProductsHandler(ProductPersistencePort port) {
        return new ListProductsHandler(port);
    }

    @Bean
    public GetProductByIdHandler getProductByIdHandler(ProductPersistencePort port) {
        return new GetProductByIdHandler(port);
    }

    @Bean
    public CreateProductHandler createProductHandler(ProductPersistencePort port) {
        return new CreateProductHandler(port);
    }

    @Bean
    public UpdateProductHandler updateProductHandler(ProductPersistencePort port, ProductPersistenceMapper persistenceMapper) {
        return new UpdateProductHandler(port, persistenceMapper);
    }

    @Bean
    public DeleteProductHandler deleteProductHandler(ProductPersistencePort port) {
        return new DeleteProductHandler(port);
    }

}
