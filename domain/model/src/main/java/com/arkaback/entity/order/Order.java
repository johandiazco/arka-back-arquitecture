package com.arkaback.entity.order;

import com.arkaback.entity.person.Person;
import com.arkaback.entity.warehouse.Warehouse;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private Long id;
    private Person person;
    private Warehouse warehouse;

    @Builder.Default
    private List<OrderDetail> details = new ArrayList<>();

    private String orderCode;
    private LocalDateTime orderDate;

    @Builder.Default
    private OrderStatu orderStatus = OrderStatu.PENDIENTE;

    //Cambia estado de la orden
    public Order withStatus(OrderStatu newOrderStatus) {
        return this.toBuilder()
                .orderStatus(newOrderStatus)
                .build();
    }

    //Actualiza detalles de la orden
    public Order withDetails(List<OrderDetail> newOrderDetails) {
        return this.toBuilder()
                .details(newOrderDetails)
                .build();
    }

    //Calcula total de la orden sumando subtotales
    public BigDecimal calculateTotal() {
        return details.stream()
                .map(OrderDetail::calculateSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    //Valida la orden contenga datos mínimos requeridos
    public void validate() {
        if (person == null) {
            throw new IllegalArgumentException("La orden debe tener un cliente asociado");
        }
        if (warehouse == null) {
            throw new IllegalArgumentException("La orden debe tener una bodega asociada");
        }
        if (details == null || details.isEmpty()) {
            throw new IllegalArgumentException("La orden debe tener al menos un producto");
        }

        // Validar cada detalle
        details.forEach(OrderDetail::validate);
    }
}
