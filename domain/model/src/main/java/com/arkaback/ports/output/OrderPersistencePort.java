package com.arkaback.ports.output;

import com.arkaback.entity.order.Order;
import java.util.List;
import java.util.Optional;

public interface OrderPersistencePort {
    Order save(Order order);
    Optional<Order> findById(Long id);
    List<Order> findAll();
    List<Order> findByPersonId(Long personId);
    String generateOrderCode();
}
