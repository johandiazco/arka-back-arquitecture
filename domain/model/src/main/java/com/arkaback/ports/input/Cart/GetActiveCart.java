package com.arkaback.ports.input.Cart;

import com.arkaback.entity.cart.ShoppingCart;
import java.util.Optional;

public interface GetActiveCart {
    Optional<ShoppingCart> execute(Long personId);
}
