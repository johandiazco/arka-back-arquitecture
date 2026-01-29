package com.arkaback.ports.input.Order;

import com.arkaback.entity.order.Order;
import java.util.List;

public interface ListOrders {
    List<Order> getAll();
}
