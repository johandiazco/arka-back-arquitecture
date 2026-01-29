package com.arkaback.ports.input.Order;

import java.util.Optional;
import com.arkaback.entity.order.Order;

public interface GetOrderById {
    Optional<Order> getById(Long id);
}
