package com.arkaback.controller;

import com.arkaback.dto.OrderCreateRequest;
import com.arkaback.dto.OrderResponse;
import com.arkaback.entity.Order;
import com.arkaback.mapper.OrderDtoMapper;
import com.arkaback.ports.input.Order.CreateOrder;
import com.arkaback.ports.input.Order.GetOrderById;
import com.arkaback.ports.input.Order.GetOrdersByPerson;
import com.arkaback.ports.input.Order.ListOrders;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderRestController {

    private final CreateOrder createOrder;
    private final GetOrderById getOrderById;
    private final ListOrders listOrders;
    private final GetOrdersByPerson getOrdersByPerson;
    private final OrderDtoMapper mapper;


    @PostMapping("/create")
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderCreateRequest request) {
        Order order = mapper.toDomain(request);
        Order created = createOrder.create(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(created));
    }

    //Obtener orden por ID
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getById(@PathVariable Long id) {
        return getOrderById.getById(id)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //Listar órdenes
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAll() {
        List<Order> orders = listOrders.getAll();
        List<OrderResponse> responses = orders.stream()
                .map(mapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    //Obtener órden de cliente
    @GetMapping("/person/{personId}")
    public ResponseEntity<List<OrderResponse>> getByPerson(@PathVariable Long personId) {
        List<Order> orders = getOrdersByPerson.getByPersonId(personId);
        List<OrderResponse> responses = orders.stream()
                .map(mapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }
}
