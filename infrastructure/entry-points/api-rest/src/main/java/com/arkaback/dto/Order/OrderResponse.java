package com.arkaback.dto.Order;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Long id;
    private String orderCode;
    private LocalDateTime orderDate;
    private String orderStatus;

    // Info cliente
    private Long personId;
    private String personName;
    private String personEmail;

    // Info warehouse
    private Long warehouseId;
    private String warehouseName;
    private String warehouseCountry;

    // Detalles orden
    private List<OrderDetailResponse> details;

    // Total calculado
    private BigDecimal total;
}
