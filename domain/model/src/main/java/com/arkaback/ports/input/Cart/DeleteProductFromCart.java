package com.arkaback.ports.input.Cart;

import com.arkaback.entity.cart.ShoppingCart;

public interface DeleteProductFromCart {
    ShoppingCart execute(Long personId, Long productId);
}
