package com.arkaback.controller;

import com.arkaback.dto.Order.OrderCreateRequest;
import com.arkaback.dto.Order.OrderResponse;
import com.arkaback.dto.Order.OrderStatusUpdateRequest;
import com.arkaback.dto.Order.OrderUpdateRequest;
import com.arkaback.entity.Order;
import com.arkaback.entity.OrderStatu;
import com.arkaback.mapper.OrderDtoMapper;
import com.arkaback.ports.input.Order.*;
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
    private final UpdateOrder updateOrder;
    private final UpdateOrderStatus updateOrderStatus;
    private final OrderDtoMapper mapper;


    @PostMapping("/create")
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderCreateRequest request) {
        Order order = mapper.toDomain(request);
        Order created = createOrder.create(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(created));
    }

    //Actualizar orden
    @PutMapping("/{orderId}")
    public ResponseEntity<OrderResponse> update(@PathVariable Long orderId, @Valid @RequestBody OrderUpdateRequest request) {
        Order order = mapper.updateToDomain(request);
        Order updated = updateOrder.update(orderId, order);
        return ResponseEntity.ok(mapper.toResponse(updated));
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

    //Actualiza estado de orden (HU6)
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody OrderStatusUpdateRequest request) {

        OrderStatu newStatus = OrderStatu.valueOf(request.getNewStatus().toUpperCase());
        Order updated = updateOrderStatus.updateStatus(orderId, newStatus);
        return ResponseEntity.ok(mapper.toResponse(updated));
    }

}
