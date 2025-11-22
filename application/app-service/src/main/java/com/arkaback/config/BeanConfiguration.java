package com.arkaback.config;

import com.arkaback.CreateProductUseCaseImpl;
import com.arkaback.ports.in.CreateProductUseCase;
import com.arkaback.ports.out.InventoryPersistencePort;
import com.arkaback.ports.out.ProductPersistencePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.arkaback")
public class BeanConfiguration {

    @Bean
    public CreateProductUseCase createProductUseCase(
            ProductPersistencePort productPersistencePort,
            InventoryPersistencePort inventoryPersistencePort) {
        return new CreateProductUseCaseImpl(productPersistencePort, inventoryPersistencePort);
    }
}
