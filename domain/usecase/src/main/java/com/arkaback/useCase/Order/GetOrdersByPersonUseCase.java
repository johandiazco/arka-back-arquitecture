package com.arkaback.useCase.Order;

import com.arkaback.entity.Order;
import com.arkaback.ports.input.Order.GetOrdersByPerson;
import com.arkaback.ports.output.OrderPersistencePort;
import lombok.AllArgsConstructor;
import java.util.List;

@AllArgsConstructor
public class GetOrdersByPersonUseCase implements GetOrdersByPerson {

    private final OrderPersistencePort orderPersistencePort;

    @Override
    public List<Order> getByPersonId(Long personId) {
        return orderPersistencePort.findByPersonId(personId);
    }
}
