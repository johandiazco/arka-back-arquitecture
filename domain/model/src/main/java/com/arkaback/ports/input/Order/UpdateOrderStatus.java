package com.arkaback.ports.input.Order;

import com.arkaback.entity.order.Order;
import com.arkaback.entity.order.OrderStatu;

public interface UpdateOrderStatus {
    //ctualiza el estado de una orden y envía notificación al cliente
    Order updateStatus(Long orderId, OrderStatu newStatus);
}
