package com.arkaback.useCase.Order;

import com.arkaback.entity.Order;
import com.arkaback.ports.input.CreateOrderImpl;
import com.arkaback.ports.output.OrderPersistencePort;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CreateOrderUseCase implements CreateOrderImpl {

    private OrderPersistencePort orderPersistencePort;

    @Override
    public Order createOrder(Order order) {
        order.validePrice();
        Order savedOrder = orderPersistencePort.create(order);
        return savedOrder;
    }

}
