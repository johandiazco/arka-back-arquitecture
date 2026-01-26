package com.arkaback.dto.Order;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusUpdateRequest {

    @NotNull(message = "El nuevo estado es obligatorio")
    private String newStatus; // "PENDIENTE", "CONFIRMADO", "ENVIADO", "ENTREGADO", "CANCELADO"
}
