package com.arkaback.useCase.Cart;

import com.arkaback.entity.cart.ShoppingCart;
import com.arkaback.exceptions.infrastructure.CartNotFoundException;
import com.arkaback.ports.input.Cart.ClearCart;
import com.arkaback.ports.output.ShoppingCartPersistencePort;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ClearCartUseCase implements ClearCart {

    private final ShoppingCartPersistencePort cartPersistencePort;

    @Override
    public ShoppingCart execute(Long personId) {

        if (personId == null || personId <= 0) {
            throw new IllegalArgumentException("El ID del cliente es inválido");
        }

        ShoppingCart cart = cartPersistencePort.findActiveCartByPersonId(personId)
                .orElseThrow(() -> new CartNotFoundException(
                        "No existe carrito activo para el cliente con ID: " + personId));

        //Elimina todos los items de los productos agregados
        cartPersistencePort.clearCartItems(cart.getId());

        //Actualiza ultima actividad del carrito
        ShoppingCart updatedCart = cart.updateLastActivity();
        cartPersistencePort.save(updatedCart);

        //devuelve carrito vacío actualizado
        return cartPersistencePort.findById(cart.getId())
                .orElseThrow(() -> new RuntimeException("Error al actualizar el carrito"));
    }
}













