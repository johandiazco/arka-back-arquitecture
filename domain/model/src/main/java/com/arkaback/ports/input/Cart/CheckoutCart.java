package com.arkaback.ports.input.Cart;

import com.arkaback.entity.order.Order;

public interface CheckoutCart {
    Order execute(Long personId, Long warehouseId);
}
