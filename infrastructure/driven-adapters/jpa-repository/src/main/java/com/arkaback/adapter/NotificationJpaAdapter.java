package com.arkaback.adapter;

import com.arkaback.entity.notification.Notification;
import com.arkaback.entity.notification.NotificationEntity;
import com.arkaback.entity.notification.NotificationStatus;
import com.arkaback.entity.notification.NotificationStatusEntity;
import com.arkaback.mappers.NotificationPersistenceMapper;
import com.arkaback.ports.output.NotificationPersistencePort;
import com.arkaback.repository.NotificationJpaRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class NotificationJpaAdapter implements NotificationPersistencePort {

    private final NotificationJpaRepository notificationJpaRepository;
    private final NotificationPersistenceMapper mapper;

    @Override
    @Transactional
    public Notification save(Notification notification) {
        NotificationEntity entity = mapper.toEntity(notification);
        NotificationEntity saved = notificationJpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional
    public Optional<Notification> findById(Long id) {
        return notificationJpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional
    public List<Notification> findByStatus(NotificationStatus status) {
        NotificationStatusEntity statusEntity = NotificationStatusEntity.valueOf(status.name());
        return notificationJpaRepository.findByStatus(statusEntity)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public List<Notification> findByOrderId(Long orderId) {
        return notificationJpaRepository.findByOrder_Id(orderId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
