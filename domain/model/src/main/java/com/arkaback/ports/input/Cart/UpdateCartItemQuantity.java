package com.arkaback.ports.input.Cart;

import com.arkaback.entity.cart.ShoppingCart;

public interface UpdateCartItemQuantity {
    ShoppingCart execute(Long personId, Long productId, Integer newQuantity);
}
