package com.arkaback.useCase.Cart;

import com.arkaback.entity.cart.CartItem;
import com.arkaback.entity.cart.ShoppingCart;
import com.arkaback.entity.inventory.Inventory;
import com.arkaback.entity.product.Product;
import com.arkaback.exceptions.domain.InsufficientStockException;
import com.arkaback.exceptions.infrastructure.ProductNotFoundException;
import com.arkaback.ports.input.Cart.AddProductToCart;
import com.arkaback.ports.output.InventoryPersistencePort;
import com.arkaback.ports.output.ProductPersistencePort;
import com.arkaback.ports.output.ShoppingCartPersistencePort;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.Optional;

@AllArgsConstructor
public class AddProductToCartUseCase implements AddProductToCart {

    private final ShoppingCartPersistencePort cartPersistencePort;
    private final ProductPersistencePort productPersistencePort;
    private final InventoryPersistencePort inventoryPersistencePort;

    @Override
    public ShoppingCart execute(Long personId, Long productId, Integer quantity) {

        validateInput(personId, productId, quantity);

        //Buscamos o creamos carrito ACTIVO del cliente
        ShoppingCart cart = cartPersistencePort.findActiveCartByPersonId(personId)
                .orElseGet(() -> cartPersistencePort.createCart(personId));

        //Verifica que el producto existe
        Product product = productPersistencePort.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(
                        "Producto no encontrado con ID: " + productId));

        //Verifica stock disponible asumiendo bodega por defecto 1
        Inventory inventory = inventoryPersistencePort
                .findByProductIdAndWarehouseId(productId, 1L)
                .orElseThrow(() -> new InsufficientStockException(
                        "No hay inventario disponible para el producto: " + product.getName()));

        int stockDisponible = inventory.getStockActual() - inventory.getStockReserved();

        //Busca el producto que ya este en el carrito
        Optional<CartItem> existingItem = cartPersistencePort
                .findCartItemByCartAndProduct(cart.getId(), productId);

        if (existingItem.isPresent()) {
            //Si el Producto ya existe sumamos cantidades
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;

            //Validamos stock para la nueva cantidad total
            if (stockDisponible < newQuantity) {
                throw new InsufficientStockException(
                        "Stock insuficiente. Disponible: " + stockDisponible +
                                ", En carrito: " + item.getQuantity() +
                                ", Intentando agregar: " + quantity);
            }

            //Actualiza cantidad del producto existente
            CartItem updatedItem = item.toBuilder()
                    .quantity(newQuantity)
                    .updatedAt(LocalDateTime.now())
                    .build();

            cartPersistencePort.updateCartItem(updatedItem);

        } else {
            //Si el producto es nuevo agregamos al carrito

            //Valida stock
            if (stockDisponible < quantity) {
                throw new InsufficientStockException(
                        "Stock insuficiente para el producto: " + product.getName() +
                                ". Disponible: " + stockDisponible +
                                ", Solicitado: " + quantity);
            }

            // Crea nuevo item de producto
            CartItem newItem = CartItem.builder()
                    .shoppingCart(cart)
                    .product(product)
                    .quantity(quantity)
                    .unitPrice(product.getPrice())
                    .addedAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            //Valida el item del producto
            newItem.validate();

            cartPersistencePort.addCartItem(newItem);
        }

        //Actualiza ultima actividad del carrito
        ShoppingCart updatedCart = cart.updateLastActivity();
        cartPersistencePort.save(updatedCart);

        //Retorna carrito completo actualizado
        return cartPersistencePort.findById(cart.getId())
                .orElseThrow(() -> new RuntimeException("Error al actualizar el carrito"));
    }

    private void validateInput(Long personId, Long productId, Integer quantity) {
        if (personId == null || personId <= 0) {
            throw new IllegalArgumentException("El ID del cliente es inválido");
        }
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("El ID del producto es inválido");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
    }
}













