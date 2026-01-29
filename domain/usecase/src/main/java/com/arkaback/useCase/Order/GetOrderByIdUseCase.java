package com.arkaback.useCase.Order;

import com.arkaback.entity.order.Order;
import com.arkaback.ports.input.Order.GetOrderById;
import com.arkaback.ports.output.OrderPersistencePort;
import lombok.AllArgsConstructor;
import java.util.Optional;

@AllArgsConstructor
public class GetOrderByIdUseCase implements GetOrderById {

    private final OrderPersistencePort orderPersistencePort;

    @Override
    public Optional<Order> getById(Long id) {
        return orderPersistencePort.findById(id);
    }
}
