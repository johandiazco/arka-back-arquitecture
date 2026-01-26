package com.arkaback.repository;

import com.arkaback.entity.NotificationEntity;
import com.arkaback.entity.NotificationStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationJpaRepository extends JpaRepository<NotificationEntity, Long> {
    List<NotificationEntity> findByStatus(NotificationStatusEntity status);
    List<NotificationEntity> findByOrder_Id(Long orderId);
}
