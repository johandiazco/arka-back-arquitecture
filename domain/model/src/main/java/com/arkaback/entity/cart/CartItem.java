package com.arkaback.entity.cart;

import com.arkaback.entity.product.Product;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    private Long id;
    private ShoppingCart shoppingCart;
    private Product product;
    private Integer quantity;
    private BigDecimal unitPrice;
    private LocalDateTime addedAt;
    private LocalDateTime updatedAt;

    //calculamos subtotal quantity * unitPrice
    public BigDecimal calculateSubtotal() {
        if (quantity == null || unitPrice == null) {
            return BigDecimal.ZERO;
        }
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

     //Valida datos correctos
    public void validate() {
        if (product == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio unitario debe ser mayor a 0");
        }
    }

    // agrega cantidad del item - additionalQuantity cantidad a sumar y retornamos artItem con cantidad actualizada
    public CartItem addQuantity(Integer additionalQuantity) {
        if (additionalQuantity == null || additionalQuantity <= 0) {
            throw new IllegalArgumentException("La cantidad a agregar debe ser mayor a 0");
        }

        return this.toBuilder()
                .quantity(this.quantity + additionalQuantity)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    //Actualiza la cantidad - newQuantity para nueva cantidad y retornamos CartItem con cantidad actualizada
    public CartItem updateQuantity(Integer newQuantity) {
        if (newQuantity == null || newQuantity <= 0) {
            throw new IllegalArgumentException("La nueva cantidad debe ser mayor a 0");
        }

        return this.toBuilder()
                .quantity(newQuantity)
                .updatedAt(LocalDateTime.now())
                .build();
    }
}













