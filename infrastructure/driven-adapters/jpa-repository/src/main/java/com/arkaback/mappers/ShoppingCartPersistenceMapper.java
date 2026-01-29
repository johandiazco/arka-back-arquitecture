package com.arkaback.mappers;

import com.arkaback.entity.cart.*;
import com.arkaback.entity.category.Category;
import com.arkaback.entity.person.Person;
import com.arkaback.entity.person.PersonEntity;
import com.arkaback.entity.product.Product;
import com.arkaback.entity.product.ProductEntity;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ShoppingCartPersistenceMapper {

    //Convertimos ShoppingCartEntity(JPA) a ShoppingCart(Dominio)
    public ShoppingCart toDomain(ShoppingCartEntity entity) {
        if (entity == null) return null;

        //Mapear Person
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

        //Mapear Items
        List<CartItem> items = new ArrayList<>();
        if (entity.getItems() != null) {
            items = entity.getItems().stream()
                    .map(this::itemToDomain)
                    .collect(Collectors.toList());
        }

        //Mapear CartStatus
        CartStatus status = CartStatus.valueOf(entity.getStatus().name());

        return ShoppingCart.builder()
                .id(entity.getId())
                .person(person)
                .status(status)
                .items(items)
                .createdAt(entity.getCreatedAt())
                .lastActivity(entity.getLastActivity())
                .build();
    }

    //Convertimos ShoppingCart(Dominio) a ShoppingCartEntity(JPA)

    public ShoppingCartEntity toEntity(ShoppingCart cart) {
        if (cart == null) return null;

        // Mapear Person
        PersonEntity personEntity = null;
        if (cart.getPerson() != null) {
            personEntity = PersonEntity.builder()
                    .id(cart.getPerson().getId())
                    .build();
        }

        //Mapea CartStatus
        CartStatusEntity statusEntity = CartStatusEntity.valueOf(cart.getStatus().name());

        //Crea ShoppingCartEntity
        ShoppingCartEntity cartEntity = ShoppingCartEntity.builder()
                .id(cart.getId())
                .person(personEntity)
                .status(statusEntity)
                .createdAt(cart.getCreatedAt())
                .lastActivity(cart.getLastActivity())
                .totalAmount(cart.calculateTotal())
                .items(new ArrayList<>())
                .build();

        //Mapea Items
        if (cart.getItems() != null) {
            List<CartItemEntity> itemEntities = cart.getItems().stream()
                    .map(item -> itemToEntity(item, cartEntity))
                    .collect(Collectors.toList());
            cartEntity.setItems(itemEntities);
        }

        return cartEntity;
    }

    //Convertimos CartItemEntity(JPA) a CartItem(Dominio)
    private CartItem itemToDomain(CartItemEntity entity) {
        if (entity == null) return null;

        //Mapea Product
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

        return CartItem.builder()
                .id(entity.getId())
                .product(product)
                .quantity(entity.getQuantity())
                .unitPrice(entity.getUnitPrice())
                .addedAt(entity.getAddedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    //Convertimos CartItem(Dominio) a CartItemEntity(JPA)
    private CartItemEntity itemToEntity(CartItem item, ShoppingCartEntity cartEntity) {
        if (item == null) return null;

        // Mapear Product
        ProductEntity productEntity = null;
        if (item.getProduct() != null) {
            productEntity = ProductEntity.builder()
                    .id(item.getProduct().getId())
                    .build();
        }

        return CartItemEntity.builder()
                .id(item.getId())
                .shoppingCart(cartEntity)
                .product(productEntity)
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .addedAt(item.getAddedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }
}