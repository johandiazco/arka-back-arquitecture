package com.arkaback.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequest {

    @NotNull(message = "El ID del cliente es obligatorio")
    private Long personId;

    @NotNull(message = "El ID de la bodega es obligatorio")
    private Long warehouseId;

    @NotEmpty(message = "La orden debe tener al menos un producto")
    @Valid
    private List<OrderDetailRequest> details;
}
