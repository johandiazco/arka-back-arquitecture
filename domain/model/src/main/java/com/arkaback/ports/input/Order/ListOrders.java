package com.arkaback.ports.input.Order;

import com.arkaback.entity.Order;
import java.util.List;

public interface ListOrders {
    List<Order> getAll();
}
