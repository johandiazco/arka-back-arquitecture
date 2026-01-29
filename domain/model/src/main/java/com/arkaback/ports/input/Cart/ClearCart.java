package com.arkaback.ports.input.Cart;

import com.arkaback.entity.cart.ShoppingCart;

public interface ClearCart {
    ShoppingCart execute(Long personId);
}