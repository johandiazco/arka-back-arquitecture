package com.arkaback.mappers;

import com.arkaback.entity.category.Category;
import com.arkaback.entity.order.*;
import com.arkaback.entity.person.Person;
import com.arkaback.entity.person.PersonEntity;
import com.arkaback.entity.product.Product;
import com.arkaback.entity.product.ProductEntity;
import com.arkaback.entity.warehouse.Warehouse;
import com.arkaback.entity.warehouse.WarehouseEntity;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderPersistenceMapper {

    public Order toDomain(OrderEntity entity) {
        if (entity == null) return null;

        // Mapear Person
        Person person = null;
        if (entity.getPerson() != null) {
            person = Person.builder()
                    .id(entity.getPerson().getId())
                    .name(entity.getPerson().getName())
                    .email(entity.getPerson().getEmail())
                    .phone(entity.getPerson().getPhone())
                    .address(entity.getPerson().getAddress())
                    .isActive(entity.getPerson().getIsActive())
                    .build();
        }

        // Mapear Warehouse
        Warehouse warehouse = null;
        if (entity.getWarehouse() != null) {
            warehouse = Warehouse.builder()
                    .id(entity.getWarehouse().getId())
                    .name(entity.getWarehouse().getName())
                    .country(entity.getWarehouse().getCountry())
                    .city(entity.getWarehouse().getCity())
                    .address(entity.getWarehouse().getAddress())
                    .phone(entity.getWarehouse().getPhone())
                    .isActive(entity.getWarehouse().getIsActive())
                    .build();
        }

        // Mapear OrderDetails
        List<OrderDetail> details = new ArrayList<>();
        if (entity.getDetails() != null) {
            details = entity.getDetails().stream()
                    .map(this::detailToDomain)
                    .collect(Collectors.toList());
        }

        // Mapear OrderStatu
        OrderStatu orderStatu = OrderStatu.valueOf(entity.getOrderStatus().name());

        return Order.builder()
                .id(entity.getId())
                .person(person)
                .warehouse(warehouse)
                .details(details)
                .orderCode(entity.getOrderCode())
                .orderDate(entity.getOrderDate())
                .orderStatus(orderStatu)
                .build();
    }

    public OrderEntity toEntity(Order order) {
        if (order == null) return null;

        // Mapear Person
        PersonEntity personEntity = null;
        if (order.getPerson() != null) {
            personEntity = PersonEntity.builder()
                    .id(order.getPerson().getId())
                    .build();
        }

        // Mapear Warehouse
        WarehouseEntity warehouseEntity = null;
        if (order.getWarehouse() != null) {
            warehouseEntity = WarehouseEntity.builder()
                    .id(order.getWarehouse().getId())
                    .build();
        }

        // Mapear OrderStatu
        OrderStatuEntity orderStatuEntity = OrderStatuEntity.valueOf(order.getOrderStatus().name());

        // Crear OrderEntity
        OrderEntity orderEntity = OrderEntity.builder()
                .id(order.getId())
                .person(personEntity)
                .warehouse(warehouseEntity)
                .orderCode(order.getOrderCode())
                .orderDate(order.getOrderDate())
                .orderStatus(orderStatuEntity)
                .details(new ArrayList<>())
                .build();

        // Mapear OrderDetails
        if (order.getDetails() != null) {
            List<OrderDetailEntity> detailEntities = order.getDetails().stream()
                    .map(detail -> detailToEntity(detail, orderEntity))
                    .collect(Collectors.toList());
            orderEntity.setDetails(detailEntities);
        }

        return orderEntity;
    }

    private OrderDetail detailToDomain(OrderDetailEntity entity) {
        if (entity == null) return null;

        Product product = null;
        if (entity.getProduct() != null) {
            Category category = null;
            if (entity.getProduct().getCategory() != null) {
                category = Category.builder()
                        .id(entity.getProduct().getCategory().getId())
                        .name(entity.getProduct().getCategory().getName())
                        .description(entity.getProduct().getCategory().getDescription())
                        .isActive(entity.getProduct().getCategory().getIsActive())
                        .build();
            }

            product = Product.builder()
                    .id(entity.getProduct().getId())
                    .name(entity.getProduct().getName())
                    .description(entity.getProduct().getDescription())
                    .price(entity.getProduct().getPrice())
                    .sku(entity.getProduct().getSku())
                    .brand(entity.getProduct().getBrand())
                    .minStock(entity.getProduct().getMinStock())
                    .isActive(entity.getProduct().getIsActive())
                    .category(category)
                    .build();
        }

        return OrderDetail.builder()
                .id(entity.getId())
                .product(product)
                .quantity(entity.getQuantity())
                .unitPrice(entity.getUnitPrice())
                .build();
    }

    private OrderDetailEntity detailToEntity(OrderDetail detail, OrderEntity orderEntity) {
        if (detail == null) return null;

        ProductEntity productEntity = null;
        if (detail.getProduct() != null) {
            productEntity = ProductEntity.builder()
                    .id(detail.getProduct().getId())
                    .build();
        }

        return OrderDetailEntity.builder()
                .id(detail.getId())
                .order(orderEntity)
                .product(productEntity)
                .quantity(detail.getQuantity())
                .unitPrice(detail.getUnitPrice())
                .build();
    }
}
