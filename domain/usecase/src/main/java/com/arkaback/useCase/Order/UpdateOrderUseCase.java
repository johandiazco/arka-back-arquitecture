package com.arkaback.useCase.Order;

import com.arkaback.entity.inventory.Inventory;
import com.arkaback.entity.order.Order;
import com.arkaback.entity.order.OrderDetail;
import com.arkaback.entity.order.OrderStatu;
import com.arkaback.exceptions.domain.InsufficientStockException;
import com.arkaback.exceptions.domain.InvalidOrderException;
import com.arkaback.exceptions.infrastructure.OrderNotFoundException;
import com.arkaback.ports.input.Order.UpdateOrder;
import com.arkaback.ports.output.InventoryPersistencePort;
import com.arkaback.ports.output.OrderPersistencePort;
import lombok.AllArgsConstructor;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
public class UpdateOrderUseCase implements UpdateOrder {

    private final OrderPersistencePort orderPersistencePort;
    private final InventoryPersistencePort inventoryPersistencePort;

    @Override
    public Order update(Long orderId, Order updatedOrder) {
        // Busca una orden existente
        Order existingOrder = orderPersistencePort.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(
                        "Orden no encontrada con ID: " + orderId));

        // Valida que esté en estado PENDIENTE
        if (existingOrder.getOrderStatus() != OrderStatu.PENDIENTE) {
            throw new InvalidOrderException(
                    "Solo se pueden modificar órdenes en estado PENDIENTE. Estado actual: "
                            + existingOrder.getOrderStatus());
        }

        // Valida la nueva orden
        updatedOrder.validate();

        // Compara detalles anteriores con los nuevos para ajustar el stock
        Map<Long, Integer> oldQuantities = existingOrder.getDetails().stream()
                .collect(Collectors.toMap(
                        detail -> detail.getProduct().getId(),
                        OrderDetail::getQuantity
                ));

        Map<Long, Integer> newQuantities = updatedOrder.getDetails().stream()
                .collect(Collectors.toMap(
                        detail -> detail.getProduct().getId(),
                        OrderDetail::getQuantity
                ));

        // Procesa los cambios de inventario
        processInventoryChanges(
                existingOrder.getWarehouse().getId(),
                oldQuantities,
                newQuantities
        );

        // Actualiza la orden manteniendo algunos datos originales
        Order orderToSave = updatedOrder.toBuilder()
                .id(existingOrder.getId())
                .orderCode(existingOrder.getOrderCode())
                .orderDate(existingOrder.getOrderDate())
                .orderStatus(existingOrder.getOrderStatus())
                .build();

        return orderPersistencePort.save(orderToSave);
    }


    private void processInventoryChanges(
            Long warehouseId,
            Map<Long, Integer> oldQuantities,
            Map<Long, Integer> newQuantities) {

        // elimina reserva de productos que no estan en la orden
        Set<Long> removedProducts = new HashSet<>(oldQuantities.keySet());
        removedProducts.removeAll(newQuantities.keySet());

        for (Long productId : removedProducts) {
            releaseStock(productId, warehouseId, oldQuantities.get(productId));
        }

        // Productos nuevos o con cantidad modificada
        for (Map.Entry<Long, Integer> entry : newQuantities.entrySet()) {
            Long productId = entry.getKey();
            Integer newQuantity = entry.getValue();
            Integer oldQuantity = oldQuantities.getOrDefault(productId, 0);

            int difference = newQuantity - oldQuantity;

            if (difference > 0) {
                // Incrementar reserva
                reserveStock(productId, warehouseId, difference);
            } else if (difference < 0) {
                // Reducir reserva
                releaseStock(productId, warehouseId, Math.abs(difference));
            }
            // Si difference == 0, no hay cambios
        }
    }

    // Reserva stock adicional
    private void reserveStock(Long productId, Long warehouseId, int quantity) {
        Inventory inventory = inventoryPersistencePort
                .findByProductIdAndWarehouseId(productId, warehouseId)
                .orElseThrow(() -> new InsufficientStockException(
                        "No hay inventario para el producto ID: " + productId));

        int stockDisponible = inventory.getStockActual() - inventory.getStockReserved();

        if (stockDisponible < quantity) {
            throw new InsufficientStockException(
                    "Stock insuficiente para producto ID: " + productId +
                            ". Disponible: " + stockDisponible + ", Solicitado: " + quantity);
        }

        Inventory updatedInventory = Inventory.builder()
                .id(inventory.getId())
                .product(inventory.getProduct())
                .stockActual(inventory.getStockActual())
                .stockReserved(inventory.getStockReserved() + quantity)
                .build();

        inventoryPersistencePort.update(updatedInventory);
    }

    // Libera stock reservado
    private void releaseStock(Long productId, Long warehouseId, int quantity) {
        Inventory inventory = inventoryPersistencePort
                .findByProductIdAndWarehouseId(productId, warehouseId)
                .orElseThrow(() -> new RuntimeException(
                        "Inventario no encontrado para producto ID: " + productId));

        Inventory updatedInventory = Inventory.builder()
                .id(inventory.getId())
                .product(inventory.getProduct())
                .stockActual(inventory.getStockActual())
                .stockReserved(Math.max(0, inventory.getStockReserved() - quantity))
                .build();

        inventoryPersistencePort.update(updatedInventory);
    }
}
