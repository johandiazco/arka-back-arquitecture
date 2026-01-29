package com.arkaback.entity.notification;

import com.arkaback.entity.person.Person;
import com.arkaback.entity.order.Order;
import com.arkaback.entity.order.OrderStatu;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    private Long id;
    private Order order;
    private Person recipient;
    private NotificationType type;
    private String subject;
    private String message;
    private NotificationChannel channel;
    private NotificationStatus status;
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;

    public static Notification createOrderStatusNotification(Order order) {
        String subject = "Actualización de tu pedido " + order.getOrderCode();
        String message = buildOrderStatusMessage(order);

        return Notification.builder()
                .order(order)
                .recipient(order.getPerson())
                .type(NotificationType.ORDER_STATUS_CHANGE)
                .subject(subject)
                .message(message)
                .channel(NotificationChannel.EMAIL)
                .status(NotificationStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private static String buildOrderStatusMessage(Order order) {
        return String.format(
                """
                Hola %s,
                
                Tu pedido %s ha cambiado de estado a: %s
                
                Fecha de la orden: %s
                Total: $%s
                
                Gracias por tu compra.
                
                Equipo Arka
                """,
                order.getPerson().getName(),
                order.getOrderCode(),
                getStatusDescription(order.getOrderStatus()),
                order.getOrderDate(),
                order.calculateTotal()
        );
    }

    private static String getStatusDescription(OrderStatu status) {
        return switch (status) {
            case PENDIENTE -> "Pendiente de confirmación";
            case CONFIRMADO -> "Confirmado y en preparación";
            case ENVIADO -> "En camino";
            case ENTREGADO -> "Entregado exitosamente";
            case CANCELADO -> "Cancelado";
        };
    }

    public Notification markAsSent() {
        return this.toBuilder()
                .status(NotificationStatus.SENT)
                .sentAt(LocalDateTime.now())
                .build();
    }

    public Notification markAsFailed() {
        return this.toBuilder()
                .status(NotificationStatus.FAILED)
                .build();
    }
}