package com.arkaback.useCase.Cart;

import com.arkaback.entity.cart.CartItem;
import com.arkaback.entity.inventory.Inventory;
import com.arkaback.entity.cart.ShoppingCart;
import com.arkaback.exceptions.domain.InsufficientStockException;
import com.arkaback.exceptions.infrastructure.CartNotFoundException;
import com.arkaback.ports.input.Cart.UpdateCartItemQuantity;
import com.arkaback.ports.output.InventoryPersistencePort;
import com.arkaback.ports.output.ShoppingCartPersistencePort;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@AllArgsConstructor
public class UpdateCartItemQuantityUseCase implements UpdateCartItemQuantity {

    private final ShoppingCartPersistencePort cartPersistencePort;
    private final InventoryPersistencePort inventoryPersistencePort;

    @Override
    public ShoppingCart execute(Long personId, Long productId, Integer newQuantity) {

        validateInput(personId, productId, newQuantity);

        //Buscamos carrito ACTIVO del cliente
        ShoppingCart cart = cartPersistencePort.findActiveCartByPersonId(personId)
                .orElseThrow(() -> new CartNotFoundException(
                        "No existe carrito activo para el cliente con ID: " + personId));

        //Busca producto a actualizar
        CartItem item = cartPersistencePort
                .findCartItemByCartAndProduct(cart.getId(), productId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El producto con ID " + productId + " no está en el carrito"));

        //Verifica stock disponible
        Inventory inventory = inventoryPersistencePort
                .findByProductIdAndWarehouseId(productId, 1L)
                .orElseThrow(() -> new InsufficientStockException(
                        "No hay inventario disponible para el producto"));

        int stockDisponible = inventory.getStockActual() - inventory.getStockReserved();

        if (stockDisponible < newQuantity) {
            throw new InsufficientStockException(
                    "Stock insuficiente. Disponible: " + stockDisponible +
                            ", Solicitado: " + newQuantity);
        }

        //Actualiza cantidad - reemplaza, no suma
        CartItem updatedItem = item.toBuilder()
                .quantity(newQuantity)
                .updatedAt(LocalDateTime.now())
                .build();

        cartPersistencePort.updateCartItem(updatedItem);

        //Actualizar ultima actividad del carrito
        ShoppingCart updatedCart = cart.updateLastActivity();
        cartPersistencePort.save(updatedCart);

        //Retorna carrito actualizado
        return cartPersistencePort.findById(cart.getId())
                .orElseThrow(() -> new RuntimeException("Error al actualizar el carrito"));
    }

    private void validateInput(Long personId, Long productId, Integer newQuantity) {
        if (personId == null || personId <= 0) {
            throw new IllegalArgumentException("El ID del cliente es inválido");
        }
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("El ID del producto es inválido");
        }
        if (newQuantity == null || newQuantity <= 0) {
            throw new IllegalArgumentException("La nueva cantidad debe ser mayor a 0");
        }
    }
}
