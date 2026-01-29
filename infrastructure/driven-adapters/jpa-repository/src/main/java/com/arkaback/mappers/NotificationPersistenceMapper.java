package com.arkaback.mappers;

import com.arkaback.entity.notification.*;
import com.arkaback.entity.order.Order;
import com.arkaback.entity.order.OrderEntity;
import com.arkaback.entity.person.Person;
import com.arkaback.entity.person.PersonEntity;
import org.springframework.stereotype.Component;

@Component
public class NotificationPersistenceMapper {

    public Notification toDomain(NotificationEntity entity) {
        if (entity == null) return null;

        Person recipient = null;
        if (entity.getRecipient() != null) {
            recipient = Person.builder()
                    .id(entity.getRecipient().getId())
                    .name(entity.getRecipient().getName())
                    .email(entity.getRecipient().getEmail())
                    .build();
        }

        Order order = null;
        if (entity.getOrder() != null) {
            order = Order.builder()
                    .id(entity.getOrder().getId())
                    .orderCode(entity.getOrder().getOrderCode())
                    .build();
        }

        return Notification.builder()
                .id(entity.getId())
                .order(order)
                .recipient(recipient)
                .type(NotificationType.valueOf(entity.getType().name()))
                .subject(entity.getSubject())
                .message(entity.getMessage())
                .channel(NotificationChannel.valueOf(entity.getChannel().name()))
                .status(NotificationStatus.valueOf(entity.getStatus().name()))
                .sentAt(entity.getSentAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public NotificationEntity toEntity(Notification notification) {
        if (notification == null) return null;

        PersonEntity recipientEntity = null;
        if (notification.getRecipient() != null) {
            recipientEntity = PersonEntity.builder()
                    .id(notification.getRecipient().getId())
                    .build();
        }

        OrderEntity orderEntity = null;
        if (notification.getOrder() != null) {
            orderEntity = OrderEntity.builder()
                    .id(notification.getOrder().getId())
                    .build();
        }

        return NotificationEntity.builder()
                .id(notification.getId())
                .order(orderEntity)
                .recipient(recipientEntity)
                .type(NotificationTypeEntity.valueOf(notification.getType().name()))
                .subject(notification.getSubject())
                .message(notification.getMessage())
                .channel(NotificationChannelEntity.valueOf(notification.getChannel().name()))
                .status(NotificationStatusEntity.valueOf(notification.getStatus().name()))
                .sentAt(notification.getSentAt())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}