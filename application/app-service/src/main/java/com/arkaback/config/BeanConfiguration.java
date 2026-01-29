package com.arkaback.config;

import com.arkaback.ports.input.Cart.*;
import com.arkaback.ports.input.Notification.SendNotification;
import com.arkaback.ports.input.Order.*;
import com.arkaback.ports.input.Product.*;
import com.arkaback.ports.input.salesReport.ExportSalesReport;
import com.arkaback.ports.input.salesReport.GenerateWeeklySalesReport;
import com.arkaback.ports.output.*;
import com.arkaback.useCase.Cart.*;
import com.arkaback.useCase.Inventory.GetLowStockProductUseCase;
import com.arkaback.useCase.Notification.SendNotificationUseCase;
import com.arkaback.useCase.Order.*;
import com.arkaback.useCase.Product.*;
import com.arkaback.useCase.Stock.UpdateStockUseCase;
import com.arkaback.useCase.salesReport.ExportSalesReportUseCase;
import com.arkaback.useCase.salesReport.GenerateWeeklySalesReportUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.arkaback")
public class BeanConfiguration {

    // BEANS PRODUCT
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

    // BEANS INVENTORY
    @Bean
    public UpdateStock updateStockUseCase(InventoryPersistencePort inventoryPersistencePort) {
        return new UpdateStockUseCase(inventoryPersistencePort);
    }

    @Bean
    public GetLowStockProduct getLowStockProductUseCase(InventoryPersistencePort inventoryPersistencePort) {
        return new GetLowStockProductUseCase(inventoryPersistencePort);
    }

    // BEANS ORDER (HU4, HU5, HU6)
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
    public GetOrdersByPerson getOrdersByPersonUseCase(OrderPersistencePort orderPersistencePort) {
        return new GetOrdersByPersonUseCase(orderPersistencePort);
    }

    // BEANS NOTIFICATION (HU6)
    @Bean
    public SendNotification sendNotificationUseCase(
            NotificationPersistencePort notificationPersistencePort,
            EmailServicePort emailServicePort) {
        return new SendNotificationUseCase(notificationPersistencePort, emailServicePort);
    }

    @Bean
    public UpdateOrderStatus updateOrderStatusUseCase(
            OrderPersistencePort orderPersistencePort,
            SendNotification sendNotification) {
        return new UpdateOrderStatusUseCase(orderPersistencePort, sendNotification);
    }

    // BEANS CART (HU8)

    @Bean
    public AddProductToCart addProductToCartUseCase(
            ShoppingCartPersistencePort cartPersistencePort,
            ProductPersistencePort productPersistencePort,
            InventoryPersistencePort inventoryPersistencePort) {
        return new AddProductToCartUseCase(cartPersistencePort, productPersistencePort, inventoryPersistencePort);
    }

    @Bean
    public DeleteProductFromCart removeProductFromCartUseCase(
            ShoppingCartPersistencePort cartPersistencePort) {
        return new DeleteProductFromCartUseCase(cartPersistencePort);
    }

    @Bean
    public GetActiveCart getActiveCartUseCase(
            ShoppingCartPersistencePort cartPersistencePort) {
        return new GetActiveCartUseCase(cartPersistencePort);
    }

    @Bean
    public UpdateCartItemQuantity updateCartItemQuantityUseCase(
            ShoppingCartPersistencePort cartPersistencePort,
            InventoryPersistencePort inventoryPersistencePort) {
        return new UpdateCartItemQuantityUseCase(cartPersistencePort, inventoryPersistencePort);
    }

    @Bean
    public ClearCart clearCartUseCase(
            ShoppingCartPersistencePort cartPersistencePort) {
        return new ClearCartUseCase(cartPersistencePort);
    }

    @Bean
    public CheckoutCart checkoutCartUseCase(
            ShoppingCartPersistencePort cartPersistencePort,
            OrderPersistencePort orderPersistencePort,
            InventoryPersistencePort inventoryPersistencePort) {
        return new CheckoutCartUseCase(cartPersistencePort, orderPersistencePort, inventoryPersistencePort);
    }

    @Bean
    public GetAbandonedCarts getAbandonedCartsUseCase(
            ShoppingCartPersistencePort cartPersistencePort) {
        return new GetAbandonedCartsUseCase(cartPersistencePort);
    }

    @Bean
    public SendAbandonedCartReminder sendAbandonedCartReminderUseCase(
            ShoppingCartPersistencePort cartPersistencePort,
            EmailServicePort emailServicePort) {
        return new SendAbandonedCartReminderUseCase(cartPersistencePort, emailServicePort);
    }

    //BEANS REPORT (HU7)

    @Bean
    public GenerateWeeklySalesReport generateWeeklySalesReportUseCase(
            SalesReportPersistencePort salesReportPersistencePort) {
        return new GenerateWeeklySalesReportUseCase(salesReportPersistencePort);
    }

    @Bean
    public ExportSalesReport exportSalesReportUseCase() {
        return new ExportSalesReportUseCase();
    }

}