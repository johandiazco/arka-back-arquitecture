package com.arkaback.mapper;

import com.arkaback.dto.cart.CartItemResponse;
import com.arkaback.dto.cart.ShoppingCartResponse;
import com.arkaback.entity.cart.CartItem;
import com.arkaback.entity.cart.ShoppingCart;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CartDtoMapper {

    public ShoppingCartResponse toResponse(ShoppingCart cart) {
        if (cart == null) return null;

        //Mapea items
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(this::itemToResponse)
                .collect(Collectors.toList());

        //Calcular totales
        Integer totalItems = cart.getItems().size();
        Integer totalQuantity = cart.getTotalItems();

        return ShoppingCartResponse.builder()
                .id(cart.getId())
                .personId(cart.getPerson() != null ? cart.getPerson().getId() : null)
                .personName(cart.getPerson() != null ? cart.getPerson().getName() : null)
                .personEmail(cart.getPerson() != null ? cart.getPerson().getEmail() : null)
                .status(cart.getStatus().name())
                .items(itemResponses)
                .totalItems(totalItems)
                .totalQuantity(totalQuantity)
                .totalAmount(cart.calculateTotal())
                .createdAt(cart.getCreatedAt())
                .lastActivity(cart.getLastActivity())
                .hoursInactive(cart.getHoursInactive())
                .build();
    }

    private CartItemResponse itemToResponse(CartItem item) {
        if (item == null) return null;

        return CartItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                .productName(item.getProduct() != null ? item.getProduct().getName() : null)
                .productSku(item.getProduct() != null ? item.getProduct().getSku() : null)
                .productBrand(item.getProduct() != null ? item.getProduct().getBrand() : null)
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(item.calculateSubtotal())
                .addedAt(item.getAddedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }
}

