package com.arkaback.ports.input.Order;

import com.arkaback.entity.Order;

public interface CreateOrder {
    Order create(Order order);
}
