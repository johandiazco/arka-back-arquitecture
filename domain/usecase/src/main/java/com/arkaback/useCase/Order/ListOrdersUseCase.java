package com.arkaback.useCase.Order;

import com.arkaback.entity.order.Order;
import com.arkaback.ports.input.Order.ListOrders;
import com.arkaback.ports.output.OrderPersistencePort;
import lombok.AllArgsConstructor;
import java.util.List;

@AllArgsConstructor
public class ListOrdersUseCase implements ListOrders {

    private final OrderPersistencePort orderPersistencePort;

    @Override
    public List<Order> getAll() {
        return orderPersistencePort.findAll();
    }
}
