package com.arkaback.config;

import com.arkaback.ports.input.Notification.SendNotification;
import com.arkaback.ports.input.Order.*;
import com.arkaback.ports.input.Product.*;
import com.arkaback.ports.output.*;
import com.arkaback.useCase.Inventory.GetLowStockProductUseCase;
import com.arkaback.useCase.Notification.SendNotificationUseCase;
import com.arkaback.useCase.Order.*;
import com.arkaback.useCase.Product.*;
import com.arkaback.useCase.Stock.UpdateStockUseCase;
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
    public ListProduct listProductUseCase(ProductPersistencePort productPersistencePort) {
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

    @Bean
    public UpdateStock updateStockUseCase(InventoryPersistencePort inventoryPersistencePort) {
        return new UpdateStockUseCase(inventoryPersistencePort);
    }

    @Bean
    public GetLowStockProduct getLowStockProductUseCase(InventoryPersistencePort inventoryPersistencePort) {
        return new GetLowStockProductUseCase(inventoryPersistencePort);
    }

    @Bean
    public CreateOrder createOrderUseCase(
            OrderPersistencePort orderPersistencePort,
            InventoryPersistencePort inventoryPersistencePort) {
        return new CreateOrderUseCase(orderPersistencePort, inventoryPersistencePort);
    }

    @Bean
    public UpdateOrder updateOrderUseCase(
            OrderPersistencePort orderPersistencePort,
            InventoryPersistencePort inventoryPersistencePort) {
        return new UpdateOrderUseCase(orderPersistencePort, inventoryPersistencePort);
    }

    @Bean
    public GetOrderById getOrderByIdUseCase(OrderPersistencePort orderPersistencePort) {
        return new GetOrderByIdUseCase(orderPersistencePort);
    }

    @Bean
    public ListOrders listOrdersUseCase(OrderPersistencePort orderPersistencePort) {
        return new ListOrdersUseCase(orderPersistencePort);
    }

    @Bean
    public SendNotification sendNotificationUseCase(
            NotificationPersistencePort notificationPersistencePort,
            EmailServicePort emailServicePort) {
        return new SendNotificationUseCase(notificationPersistencePort, emailServicePort);
    }

    @Bean
    public GetOrdersByPerson getOrdersByPersonUseCase(OrderPersistencePort orderPersistencePort) {
        return new GetOrdersByPersonUseCase(orderPersistencePort);
    }
}