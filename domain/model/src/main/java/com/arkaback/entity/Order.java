package com.arkaback.entity;

import com.arkaback.exceptions.InvalidOrderException;
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
public class Order {
    private Long id;
    private Person person;
    private Warehouse warehouse;

    @Builder.Default
    private List<OrderDetail> detail = new ArrayList<>();

    private String orderCode;
    private LocalDateTime orderDate;
    private OrderStatu orderStatus;

    public Order withStatus(OrderStatu newOrderStatus) {
        return this.toBuilder()
                .orderStatus(newOrderStatus)
                .build();
    }

    public Order withDetail(List<OrderDetail> newOrderDetails) {
        return this.toBuilder()
                .detail(newOrderDetails)
                .build();
    }
    
    public BigDecimal calculateTotal() {
        return detail.stream()
                .map(OrderDetail::calculateSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /*
    public calculateSubtotal

     */

}
