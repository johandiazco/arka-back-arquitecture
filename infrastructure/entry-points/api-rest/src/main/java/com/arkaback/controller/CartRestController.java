package com.arkaback.controller;

import com.arkaback.dto.Order.OrderResponse;
import com.arkaback.dto.cart.AddToCartRequest;
import com.arkaback.dto.cart.CheckoutCartRequest;
import com.arkaback.dto.cart.ShoppingCartResponse;
import com.arkaback.dto.cart.UpdateCartItemRequest;
import com.arkaback.entity.cart.ShoppingCart;
import com.arkaback.entity.order.Order;
import com.arkaback.mapper.CartDtoMapper;
import com.arkaback.mapper.OrderDtoMapper;
import com.arkaback.ports.input.Cart.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartRestController {

    private final AddProductToCart addProductToCart;
    private final DeleteProductFromCart deleteProductFromCart;
    private final UpdateCartItemQuantity updateCartItemQuantity;
    private final GetActiveCart getActiveCart;
    private final ClearCart clearCart;
    private final CheckoutCart checkoutCart;
    private final GetAbandonedCarts getAbandonedCarts;
    private final SendAbandonedCartReminder sendAbandonedCartReminder;
    private final CartDtoMapper cartMapper;
    private final OrderDtoMapper orderMapper;

    //Agrega producto al carrito activo del cliente. Si el producto ya existe, suma las cantidades
    @PostMapping("/items")
    public ResponseEntity<ShoppingCartResponse> addToCart(
            @RequestParam Long personId,
            @Valid @RequestBody AddToCartRequest request) {

        ShoppingCart cart = addProductToCart.execute(
                personId,
                request.getProductId(),
                request.getQuantity()
        );

        return ResponseEntity.ok(cartMapper.toResponse(cart));
    }

    //Actualiza cantidad de un producto en el carrito y reemplaza la cantidad
    @PutMapping("/items/{productId}")
    public ResponseEntity<ShoppingCartResponse> updateCartItem(
            @RequestParam Long personId,
            @PathVariable Long productId,
            @Valid @RequestBody UpdateCartItemRequest request) {

        ShoppingCart cart = updateCartItemQuantity.execute(
                personId,
                productId,
                request.getQuantity()
        );

        return ResponseEntity.ok(cartMapper.toResponse(cart));
    }

    //Elimina producto del carrito
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<ShoppingCartResponse> deleteFromCart(
            @RequestParam Long personId,
            @PathVariable Long productId) {

        ShoppingCart cart = deleteProductFromCart.execute(personId, productId);
        return ResponseEntity.ok(cartMapper.toResponse(cart));
    }

    //Obtiene el carrito activo del cliente
    @GetMapping
    public ResponseEntity<ShoppingCartResponse> getCart(@RequestParam Long personId) {
        return getActiveCart.execute(personId)
                .map(cartMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //Vacía el carrito
    @DeleteMapping
    public ResponseEntity<ShoppingCartResponse> clearCart(@RequestParam Long personId) {
        ShoppingCart cart = clearCart.execute(personId);
        return ResponseEntity.ok(cartMapper.toResponse(cart));
    }

    //Convierte el carrito en una orden de compra, Reserva stock,
    // Marca carrito como comprado y Crea orden con estado pendiente
    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(
            @RequestParam Long personId,
            @Valid @RequestBody CheckoutCartRequest request) {

        Order order = checkoutCart.execute(personId, request.getWarehouseId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderMapper.toResponse(order));
    }

    //Lista todos los carritos abandonados >24h sin actividad. solo admin
    @GetMapping("/abandoned")
    public ResponseEntity<List<ShoppingCartResponse>> getAbandonedCarts(
            @RequestParam(defaultValue = "24") Integer hoursThreshold) {

        List<ShoppingCart> carts = getAbandonedCarts.execute(hoursThreshold);
        List<ShoppingCartResponse> responses = carts.stream()
                .map(cartMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    //Envía email recordatorio a un carrito abandonado específico. solo admin
    @PostMapping("/abandoned/notify/{cartId}")
    public ResponseEntity<String> notifyAbandonedCart(@PathVariable Long cartId) {
        log.info("Enviando recordatorio de carrito abandonado: {}", cartId);
        boolean sent = sendAbandonedCartReminder.execute(cartId);

        if (sent) {
            log.info("Email enviado exitosamente para carrito: {}", cartId);
            return ResponseEntity.ok("Email enviado exitosamente");
        } else {
            log.error("Error al enviar email para carrito: {}", cartId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al enviar el email");
        }
    }

    //Envía emails recordatorios a todos los carritos abandonados. solo admin
    @PostMapping("/abandoned/notify-all")
    public ResponseEntity<String> notifyAllAbandonedCarts() {
        log.info("Iniciando envío masivo de recordatorios de carritos abandonados");
        Integer sentCount = sendAbandonedCartReminder.sendToAllAbandoned();
        log.info("Finalizado envío masivo. Emails enviados: {}", sentCount);
        return ResponseEntity.ok(
                String.format("Se enviaron %d emails recordatorios exitosamente", sentCount)
        );
    }
}