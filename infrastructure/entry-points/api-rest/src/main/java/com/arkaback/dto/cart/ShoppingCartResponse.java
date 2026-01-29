package com.arkaback.dto.cart;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCartResponse {

    private Long id;
    //Informacion del cliente
    private Long personId;
    private String personName;
    private String personEmail;

    private String status;

    //Items u productos del carrito
    private List<CartItemResponse> items;

    private Integer totalItems;       // Cantidad total de productos
    private Integer totalQuantity;    // Suma de cantidades
    private BigDecimal totalAmount;

    private LocalDateTime createdAt;
    private LocalDateTime lastActivity;

    // Informacion para carritos abandonados
    private Long hoursInactive;
}













