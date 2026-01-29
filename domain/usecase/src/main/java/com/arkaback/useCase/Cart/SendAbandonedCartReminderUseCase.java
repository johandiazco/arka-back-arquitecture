package com.arkaback.useCase.Cart;

import com.arkaback.entity.cart.ShoppingCart;
import com.arkaback.exceptions.infrastructure.CartNotFoundException;
import com.arkaback.ports.input.Cart.SendAbandonedCartReminder;
import com.arkaback.ports.output.EmailServicePort;
import com.arkaback.ports.output.ShoppingCartPersistencePort;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
public class SendAbandonedCartReminderUseCase implements SendAbandonedCartReminder {

    private final ShoppingCartPersistencePort cartPersistencePort;
    private final EmailServicePort emailServicePort;

    @Override
    public boolean execute(Long cartId) {

        //Valida entrada
        if (cartId == null || cartId <= 0) {
            throw new IllegalArgumentException("El ID del carrito es inválido");
        }

        //Busca el carrito
        ShoppingCart cart = cartPersistencePort.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException(
                        "Carrito no encontrado con ID: " + cartId));

        //Valida tener items u productos y cliente con email
        if (cart.isEmpty()) {
            return false;
        }

        if (cart.getPerson() == null || cart.getPerson().getEmail() == null) {
            return false;
        }

        //Construye el mensaje del email
        String subject = "¡Tienes productos esperándote en tu carrito! 🛒";
        String body = buildEmailBody(cart);

        //Envia el email
        boolean sent = emailServicePort.sendEmail(
                cart.getPerson().getEmail(),
                subject,
                body
        );

        return sent;
    }

    @Override
    public Integer sendToAllAbandoned() {

        //Obtenemos todos los carritos abandonados >24h
        LocalDateTime threshold = LocalDateTime.now().minusHours(24);
        List<ShoppingCart> abandonedCarts = cartPersistencePort.findAbandonedCarts(threshold);

        //Envia email a cada uno
        int successCount = 0;

        for (ShoppingCart cart : abandonedCarts) {
            try {
                boolean sent = execute(cart.getId());
                if (sent) {
                    successCount++;
                }
            } catch (Exception e) {
                // Continua con el otro carrito
            }
        }

        return successCount;
    }

    // Construye cuerpo del email con información del carrito
    private String buildEmailBody(ShoppingCart cart) {
        StringBuilder body = new StringBuilder();

        body.append("Hola ").append(cart.getPerson().getName()).append(",\n\n");
        body.append("Notamos que dejaste algunos productos en tu carrito.\n");
        body.append("¡No pierdas la oportunidad de completar tu compra!\n\n");

        body.append("Productos en tu carrito:\n");
        body.append("─".repeat(50)).append("\n");

        cart.getItems().forEach(item -> {
            body.append(String.format("• %s (x%d) - $%s\n",
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getUnitPrice()));
        });

        body.append("─".repeat(50)).append("\n");

        BigDecimal total = cart.calculateTotal();
        body.append(String.format("\n💰 Total: $%s\n", total));
        body.append(String.format("📊 Total de items: %d\n\n", cart.getTotalItems()));

        long hoursInactive = cart.getHoursInactive();
        body.append(String.format("⏰ Tu carrito lleva %d horas esperando.\n\n", hoursInactive));

        body.append("👉 Completa tu compra ahora y recibe tus productos pronto.\n\n");
        body.append("¡Gracias por confiar en Arka!\n\n");
        body.append("Equipo Arka - Accesorios para PC");

        return body.toString();
    }
}