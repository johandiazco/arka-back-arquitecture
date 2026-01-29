package com.arkaback.ports.input.Cart;

import com.arkaback.entity.cart.ShoppingCart;
import java.util.List;

public interface GetAbandonedCarts {
    List<ShoppingCart> execute();
    List<ShoppingCart> execute(Integer hoursThreshold);
}
