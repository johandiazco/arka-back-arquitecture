package com.arkaback.dto.cart;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutCartRequest {

    @NotNull(message = "El ID de la bodega es obligatorio")
    private Long warehouseId;
}
