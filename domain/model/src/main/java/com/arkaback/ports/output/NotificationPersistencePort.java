package com.arkaback.ports.output;

import com.arkaback.entity.Notification;
import com.arkaback.entity.NotificationStatus;
import java.util.List;
import java.util.Optional;

public interface NotificationPersistencePort {

    Notification save(Notification notification);
    Optional<Notification> findById(Long id);
    List<Notification> findByStatus(NotificationStatus status);
    List<Notification> findByOrderId(Long orderId);
}
