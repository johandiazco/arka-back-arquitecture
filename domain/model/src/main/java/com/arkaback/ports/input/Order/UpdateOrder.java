
package com.arkaback.ports.input.Order;

import com.arkaback.entity.Order;

public interface UpdateOrder {
    Order update(Long orderId, Order updatedOrder);
}