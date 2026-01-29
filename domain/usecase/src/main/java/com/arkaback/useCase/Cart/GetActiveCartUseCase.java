package com.arkaback.useCase.Cart;

import com.arkaback.entity.cart.ShoppingCart;
import com.arkaback.ports.input.Cart.GetActiveCart;
import com.arkaback.ports.output.ShoppingCartPersistencePort;
import lombok.AllArgsConstructor;
import java.util.Optional;

@AllArgsConstructor
public class GetActiveCartUseCase implements GetActiveCart {

    private final ShoppingCartPersistencePort cartPersistencePort;

    @Override
    public Optional<ShoppingCart> execute(Long personId) {

        if (personId == null || personId <= 0) {
            throw new IllegalArgumentException("El ID del cliente es inválido");
        }

        //Busca y retornar carrito activo
        return cartPersistencePort.findActiveCartByPersonId(personId);
    }
}
