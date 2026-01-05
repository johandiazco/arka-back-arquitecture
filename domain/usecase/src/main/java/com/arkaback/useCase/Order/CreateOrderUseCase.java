package com.arkaback.useCase.Order;

import com.arkaback.entity.Inventory;
import com.arkaback.entity.Order;
import com.arkaback.entity.OrderDetail;
import com.arkaback.entity.OrderStatu;
import com.arkaback.exceptions.InsufficientStockException;
import com.arkaback.ports.input.Order.CreateOrder;
import com.arkaback.ports.output.InventoryPersistencePort;
import com.arkaback.ports.output.OrderPersistencePort;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class CreateOrderUseCase implements CreateOrder {

    private final OrderPersistencePort orderPersistencePort;
    private final InventoryPersistencePort inventoryPersistencePort;

    @Override
    public Order create(Order order) {

        order.validate();
        List<Inventory> inventoriesToUpdate = new ArrayList<>();

        for (OrderDetail detail : order.getDetails()) {
            Inventory inventory = inventoryPersistencePort
                    .findByProductIdAndWarehouseId(
                            detail.getProduct().getId(),
                            order.getWarehouse().getId()
                    )
                    .orElseThrow(() -> new InsufficientStockException(
                            "No hay inventario para el producto: " + detail.getProduct().getName()
                                    + " en la bodega seleccionada"
                    ));

            int stockDisponible = inventory.getStockActual() - inventory.getStockReserved();

            if (stockDisponible < detail.getQuantity()) {
                throw new InsufficientStockException(
                        "Stock insuficiente para el producto: " + detail.getProduct().getName() +
                                ". Disponible: " + stockDisponible + ", Solicitado: " + detail.getQuantity()
                );
            }

            inventoriesToUpdate.add(inventory);
        }

        String orderCode = orderPersistencePort.generateOrderCode();
        Order orderToSave = order.toBuilder()
                .orderCode(orderCode)
                .orderDate(LocalDateTime.now())
                .orderStatus(OrderStatu.PENDIENTE)
                .build();

        Order savedOrder = orderPersistencePort.save(orderToSave);

        for (int i = 0; i < savedOrder.getDetails().size(); i++) {
            OrderDetail detail = savedOrder.getDetails().get(i);
            Inventory inventory = inventoriesToUpdate.get(i);

            Inventory updatedInventory = Inventory.builder()
                    .id(inventory.getId())
                    .product(inventory.getProduct())
                    .stockActual(inventory.getStockActual())
                    .stockReserved(inventory.getStockReserved() + detail.getQuantity())
                    .build();

            inventoryPersistencePort.update(updatedInventory);
        }

        return savedOrder;
    }
}
