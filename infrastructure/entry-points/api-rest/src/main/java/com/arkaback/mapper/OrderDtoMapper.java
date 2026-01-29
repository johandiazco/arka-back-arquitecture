package com.arkaback.mapper;

import com.arkaback.dto.Order.*;
import com.arkaback.entity.order.Order;
import com.arkaback.entity.order.OrderDetail;
import com.arkaback.entity.person.Person;
import com.arkaback.entity.product.Product;
import com.arkaback.entity.warehouse.Warehouse;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderDtoMapper {

    //Convierte OrderCreateRequest a Order (dominio)
    public Order toDomain(OrderCreateRequest request) {
        if (request == null) return null;

        Person person = Person.builder()
                .id(request.getPersonId())
                .build();

        Warehouse warehouse = Warehouse.builder()
                .id(request.getWarehouseId())
                .build();

        List<OrderDetail> details = request.getDetails().stream()
                .map(this::detailToDomain)
                .collect(Collectors.toList());

        return Order.builder()
                .person(person)
                .warehouse(warehouse)
                .details(details)
                .build();
    }

    // ✨ NUEVO: Convierte OrderUpdateRequest a Order (dominio)
    public Order updateToDomain(OrderUpdateRequest request) {
        if (request == null) return null;

        Person person = Person.builder()
                .id(request.getPersonId())
                .build();

        Warehouse warehouse = Warehouse.builder()
                .id(request.getWarehouseId())
                .build();

        List<OrderDetail> details = request.getDetails().stream()
                .map(this::detailToDomain)
                .collect(Collectors.toList());

        return Order.builder()
                .person(person)
                .warehouse(warehouse)
                .details(details)
                .build();
    }

    //Convierte Order (dominio) a OrderResponse
    public OrderResponse toResponse(Order order) {
        if (order == null) return null;

        List<OrderDetailResponse> detailResponses = order.getDetails().stream()
                .map(this::detailToResponse)
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .orderDate(order.getOrderDate())
                .orderStatus(order.getOrderStatus().name())
                .personId(order.getPerson() != null ? order.getPerson().getId() : null)
                .personName(order.getPerson() != null ? order.getPerson().getName() : null)
                .personEmail(order.getPerson() != null ? order.getPerson().getEmail() : null)
                .warehouseId(order.getWarehouse() != null ? order.getWarehouse().getId() : null)
                .warehouseName(order.getWarehouse() != null ? order.getWarehouse().getName() : null)
                .warehouseCountry(order.getWarehouse() != null ? order.getWarehouse().getCountry() : null)
                .details(detailResponses)
                .total(order.calculateTotal())
                .build();
    }

    //Convierte OrderDetailRequest a OrderDetail (dominio)
    private OrderDetail detailToDomain(OrderDetailRequest request) {
        if (request == null) return null;

        Product product = Product.builder()
                .id(request.getProductId())
                .build();

        return OrderDetail.builder()
                .product(product)
                .quantity(request.getQuantity())
                .unitPrice(request.getUnitPrice())
                .build();
    }

    //Convierte OrderDetail (dominio) a OrderDetailResponse
    private OrderDetailResponse detailToResponse(OrderDetail detail) {
        if (detail == null) return null;

        return OrderDetailResponse.builder()
                .id(detail.getId())
                .productId(detail.getProduct() != null ? detail.getProduct().getId() : null)
                .productName(detail.getProduct() != null ? detail.getProduct().getName() : null)
                .productSku(detail.getProduct() != null ? detail.getProduct().getSku() : null)
                .quantity(detail.getQuantity())
                .unitPrice(detail.getUnitPrice())
                .subTotal(detail.calculateSubTotal())
                .build();
    }
}