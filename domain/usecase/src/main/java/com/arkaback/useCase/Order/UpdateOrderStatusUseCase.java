package com.arkaback.useCase.Order;

import com.arkaback.entity.notification.Notification;
import com.arkaback.entity.order.Order;
import com.arkaback.entity.order.OrderStatu;
import com.arkaback.exceptions.infrastructure.OrderNotFoundException;
import com.arkaback.ports.input.Notification.SendNotification;
import com.arkaback.ports.input.Order.UpdateOrderStatus;
import com.arkaback.ports.output.OrderPersistencePort;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UpdateOrderStatusUseCase implements UpdateOrderStatus {

    private final OrderPersistencePort orderPersistencePort;
    private final SendNotification sendNotification;

    @Override
    public Order updateStatus(Long orderId, OrderStatu newStatus) {
        // Busca la orden
        Order order = orderPersistencePort.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(
                        "Orden no encontrada con ID: " + orderId));

        // Actualiza estado
        Order updatedOrder = order.withStatus(newStatus);
        Order savedOrder = orderPersistencePort.save(updatedOrder);

        // Crea y envia notificación
        Notification notification = Notification.createOrderStatusNotification(savedOrder);
        sendNotification.send(notification);

        return savedOrder;
    }
}
