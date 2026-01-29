package com.arkaback.entity.cart;

import com.arkaback.entity.person.Person;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCart {

    private Long id;
    private Person person;

    @Builder.Default
    private CartStatus status = CartStatus.ACTIVE;

    @Builder.Default
    private List<CartItem> items = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime lastActivity;

    //Calcula el total del carrito
    public BigDecimal calculateTotal() {
        return items.stream()
                .map(CartItem::calculateSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    //Obtenemos cantidad total productos en el carrito
    public Integer getTotalItems() {
        return items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    //Buscamos producto específico en el carrito

    public Optional<CartItem> findItemByProductId(Long productId) {
        return items.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();
    }

    //Verificamos si el carrito contiene un producto específico
    public boolean containsProduct(Long productId) {
        return findItemByProductId(productId).isPresent();
    }

    //Agrega nuevo producto al carrito
    public ShoppingCart addItem(CartItem item) {
        List<CartItem> updatedItems = new ArrayList<>(this.items);
        updatedItems.add(item);

        return this.toBuilder()
                .items(updatedItems)
                .lastActivity(LocalDateTime.now())
                .build();
    }

    //Actualiza producto existente en el carrito
    public ShoppingCart updateItem(CartItem updatedItem) {
        List<CartItem> updatedItems = new ArrayList<>();

        for (CartItem item : this.items) {
            if (item.getId().equals(updatedItem.getId())) {
                updatedItems.add(updatedItem);
            } else {
                updatedItems.add(item);
            }
        }

        return this.toBuilder()
                .items(updatedItems)
                .lastActivity(LocalDateTime.now())
                .build();
    }

    //Elimina un producto del carrito por ID de producto
    public ShoppingCart removeItemByProductId(Long productId) {
        List<CartItem> updatedItems = this.items.stream()
                .filter(item -> !item.getProduct().getId().equals(productId))
                .toList();

        return this.toBuilder()
                .items(new ArrayList<>(updatedItems))
                .lastActivity(LocalDateTime.now())
                .build();
    }

    //Limpia todos los productos del carrito
    public ShoppingCart clearItems() {
        return this.toBuilder()
                .items(new ArrayList<>())
                .lastActivity(LocalDateTime.now())
                .build();
    }

    //Cambia estado del carrito
    public ShoppingCart withStatus(CartStatus newStatus) {
        return this.toBuilder()
                .status(newStatus)
                .lastActivity(LocalDateTime.now())
                .build();
    }

    //cambia estado del carrito como comprado
    public ShoppingCart markAsPurchased() {
        return withStatus(CartStatus.PURCHASED);
    }

    //marca el carrito como abandonado
    public ShoppingCart markAsAbandoned() {
        return withStatus(CartStatus.ABANDONED);
    }

    //Actualiza última actividad del carrito
    public ShoppingCart updateLastActivity() {
        return this.toBuilder()
                .lastActivity(LocalDateTime.now())
                .build();
    }

    //Verificamos si el carrito está abandonado si >24h sin actividad
    public boolean isAbandoned() {
        if (lastActivity == null || status != CartStatus.ACTIVE) {
            return false;
        }

        LocalDateTime threshold = LocalDateTime.now().minusHours(24);
        return lastActivity.isBefore(threshold);
    }

    //optenemos las horas de inactividad
    public long getHoursInactive() {
        if (lastActivity == null) {
            return 0;
        }

        return java.time.Duration.between(lastActivity, LocalDateTime.now()).toHours();
    }

    //Validamos que el carrito tenga datos mínimos requeridos
    public void validate() {
        if (person == null) {
            throw new IllegalArgumentException("El carrito debe tener un cliente asociado");
        }
        if (items.isEmpty()) {
            throw new IllegalArgumentException("El carrito debe tener al menos un producto");
        }

        //Validamos cada producto
        items.forEach(CartItem::validate);
    }

    //verificamos si el carrito está vacío
    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }

    //Verificamos si el carrito está activo
    public boolean isActive() {
        return status == CartStatus.ACTIVE;
    }
}

