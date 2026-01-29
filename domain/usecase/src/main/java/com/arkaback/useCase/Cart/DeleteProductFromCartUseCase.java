package com.arkaback.useCase.Cart;

import com.arkaback.entity.cart.CartItem;
import com.arkaback.entity.cart.ShoppingCart;
import com.arkaback.exceptions.infrastructure.CartNotFoundException;
import com.arkaback.ports.input.Cart.DeleteProductFromCart;
import com.arkaback.ports.output.ShoppingCartPersistencePort;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DeleteProductFromCartUseCase implements DeleteProductFromCart {

    private final ShoppingCartPersistencePort cartPersistencePort;

    @Override
    public ShoppingCart execute(Long personId, Long productId) {
        if (personId == null || personId <= 0) {
            throw new IllegalArgumentException("El ID del cliente es inválido");
        }
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("El ID del producto es inválido");
        }

        //Busca carrito activo del cliente
        ShoppingCart cart = cartPersistencePort.findActiveCartByPersonId(personId)
                .orElseThrow(() -> new CartNotFoundException(
                        "No existe carrito activo para el cliente con ID: " + personId));

        //Buscar el producto a eliminar en el carrito
        CartItem item = cartPersistencePort
                .findCartItemByCartAndProduct(cart.getId(), productId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El producto con ID " + productId + " no está en el carrito"));

        //Eliminar el producto
        cartPersistencePort.deleteCartItem(item.getId());

        //Actualizar ultima actividad del carrito
        ShoppingCart updatedCart = cart.updateLastActivity();
        cartPersistencePort.save(updatedCart);

        //devuelve carrito actualizado
        return cartPersistencePort.findById(cart.getId())
                .orElseThrow(() -> new RuntimeException("Error al actualizar el carrito"));
    }
}
