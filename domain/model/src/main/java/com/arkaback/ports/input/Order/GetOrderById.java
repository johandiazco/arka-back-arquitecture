package com.arkaback.ports.input.Order;

import java.util.Optional;
import com.arkaback.entity.Order;

public interface GetOrderById {
    Optional<Order> getById(Long id);
}
