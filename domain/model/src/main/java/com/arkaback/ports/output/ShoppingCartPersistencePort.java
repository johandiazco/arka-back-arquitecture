package com.arkaback.ports.output;

import com.arkaback.entity.cart.CartItem;
import com.arkaback.entity.cart.CartStatus;
import com.arkaback.entity.cart.ShoppingCart;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ShoppingCartPersistencePort {
    ShoppingCart save(ShoppingCart cart);
    Optional<ShoppingCart> findById(Long id);
    Optional<ShoppingCart> findActiveCartByPersonId(Long personId);
    ShoppingCart createCart(Long personId);
    void deleteCart(Long cartId);
    List<ShoppingCart> findByStatus(CartStatus status);
    List<ShoppingCart> findAbandonedCarts(LocalDateTime thresholdDate);
    ShoppingCart updateStatus(Long cartId, CartStatus newStatus);
    CartItem addCartItem(CartItem item);
    CartItem updateCartItem(CartItem item);
    void deleteCartItem(Long cartItemId);
    Optional<CartItem> findCartItemByCartAndProduct(Long cartId, Long productId);
    List<CartItem> findItemsByCartId(Long cartId);
    void clearCartItems(Long cartId);
}
