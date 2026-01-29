package com.arkaback.useCase.Cart;

import com.arkaback.entity.cart.ShoppingCart;
import com.arkaback.ports.input.Cart.GetAbandonedCarts;
import com.arkaback.ports.output.ShoppingCartPersistencePort;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
public class GetAbandonedCartsUseCase implements GetAbandonedCarts {

    private final ShoppingCartPersistencePort cartPersistencePort;
    private static final int DEFAULT_HOURS_THRESHOLD = 24;

    @Override
    public List<ShoppingCart> execute() {
        return execute(DEFAULT_HOURS_THRESHOLD);
    }

    @Override
    public List<ShoppingCart> execute(Integer hoursThreshold) {

        // Valida entrada
        if (hoursThreshold == null || hoursThreshold <= 0) {
            throw new IllegalArgumentException("El umbral de horas debe ser mayor a 0");
        }

        // Calculamos fecha límite; example: son las 14:00 del 28-01-2025 y hoursThreshold = 24
        // thresholdDate = 14:00 del 27-01-2025
        LocalDateTime thresholdDate = LocalDateTime.now().minusHours(hoursThreshold);

        // Busca carritos con ultima actividad antes de thresholdDate
        List<ShoppingCart> abandonedCarts = cartPersistencePort
                .findAbandonedCarts(thresholdDate);

        return abandonedCarts;
    }
}













