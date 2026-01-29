package com.arkaback.repository;

import com.arkaback.entity.order.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findByPerson_Id(Long personId);
    Optional<OrderEntity> findByOrderCode(String orderCode);
    boolean existsByOrderCode(String orderCode);
}
