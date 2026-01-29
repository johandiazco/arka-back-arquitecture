package com.arkaback.adapter;

import com.arkaback.entity.cart.*;
import com.arkaback.entity.person.PersonEntity;
import com.arkaback.entity.product.ProductEntity;
import com.arkaback.mappers.ShoppingCartPersistenceMapper;
import com.arkaback.ports.output.ShoppingCartPersistencePort;
import com.arkaback.repository.CartItemJpaRepository;
import com.arkaback.repository.PersonJpaRepository;
import com.arkaback.repository.ShoppingCartJpaRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ShoppingCartJpaAdapter implements ShoppingCartPersistencePort {

    private final ShoppingCartJpaRepository cartRepository;
    private final CartItemJpaRepository cartItemRepository;
    private final PersonJpaRepository personRepository;
    private final ShoppingCartPersistenceMapper mapper;

    @Override
    @Transactional
    public ShoppingCart save(ShoppingCart cart) {
        ShoppingCartEntity entity = mapper.toEntity(cart);
        ShoppingCartEntity saved = cartRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional
    public Optional<ShoppingCart> findById(Long id) {
        return cartRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional
    public Optional<ShoppingCart> findActiveCartByPersonId(Long personId) {
        return cartRepository.findByPerson_IdAndStatus(personId, CartStatusEntity.ACTIVE)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional
    public ShoppingCart createCart(Long personId) {
        // Verificar que la persona existe
        PersonEntity person = personRepository.findById(personId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + personId));

        // Crear nuevo carrito
        ShoppingCartEntity cartEntity = ShoppingCartEntity.builder()
                .person(person)
                .status(CartStatusEntity.ACTIVE)
                .createdAt(LocalDateTime.now())
                .lastActivity(LocalDateTime.now())
                .build();

        ShoppingCartEntity saved = cartRepository.save(cartEntity);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional
    public void deleteCart(Long cartId) {
        cartRepository.deleteById(cartId);
    }

    @Override
    @Transactional
    public List<ShoppingCart> findByStatus(CartStatus status) {
        CartStatusEntity statusEntity = CartStatusEntity.valueOf(status.name());
        return cartRepository.findByStatus(statusEntity)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ShoppingCart> findAbandonedCarts(LocalDateTime thresholdDate) {
        return cartRepository.findAbandonedCarts(thresholdDate)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ShoppingCart updateStatus(Long cartId, CartStatus newStatus) {
        ShoppingCartEntity cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado con ID: " + cartId));

        CartStatusEntity statusEntity = CartStatusEntity.valueOf(newStatus.name());
        cart.setStatus(statusEntity);
        cart.setLastActivity(LocalDateTime.now());

        ShoppingCartEntity updated = cartRepository.save(cart);
        return mapper.toDomain(updated);
    }

    @Override
    @Transactional
    public CartItem addCartItem(CartItem item) {
        // Buscar el carrito
        ShoppingCartEntity cart = cartRepository.findById(item.getShoppingCart().getId())
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        // Crear CartItemEntity
        CartItemEntity itemEntity = CartItemEntity.builder()
                .shoppingCart(cart)
                .product(ProductEntity.builder().id(item.getProduct().getId()).build())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .addedAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        CartItemEntity saved = cartItemRepository.save(itemEntity);

        // Actualizar ultima actividad del carrito
        cart.setLastActivity(LocalDateTime.now());
        cartRepository.save(cart);

        return mapper.toDomain(cartRepository.findById(cart.getId()).get())
                .getItems().stream()
                .filter(i -> i.getId().equals(saved.getId()))
                .findFirst()
                .orElse(null);
    }

    @Override
    @Transactional
    public CartItem updateCartItem(CartItem item) {
        CartItemEntity existingItem = cartItemRepository.findById(item.getId())
                .orElseThrow(() -> new RuntimeException("Item no encontrado con ID: " + item.getId()));

        existingItem.setQuantity(item.getQuantity());
        existingItem.setUpdatedAt(LocalDateTime.now());

        CartItemEntity updated = cartItemRepository.save(existingItem);

        // Actualizar ultima actividad del carrito
        ShoppingCartEntity cart = updated.getShoppingCart();
        cart.setLastActivity(LocalDateTime.now());
        cartRepository.save(cart);

        return mapper.toDomain(cartRepository.findById(cart.getId()).get())
                .getItems().stream()
                .filter(i -> i.getId().equals(updated.getId()))
                .findFirst()
                .orElse(null);
    }

    @Override
    @Transactional
    public void deleteCartItem(Long cartItemId) {
        CartItemEntity item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado con ID: " + cartItemId));

        ShoppingCartEntity cart = item.getShoppingCart();

        cartItemRepository.deleteById(cartItemId);

        // Actualizar ultima actividad del carrito
        cart.setLastActivity(LocalDateTime.now());
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Optional<CartItem> findCartItemByCartAndProduct(Long cartId, Long productId) {
        return cartItemRepository.findByShoppingCart_IdAndProduct_Id(cartId, productId)
                .map(entity -> mapper.toDomain(cartRepository.findById(cartId).get())
                        .getItems().stream()
                        .filter(item -> item.getProduct().getId().equals(productId))
                        .findFirst()
                        .orElse(null));
    }

    @Override
    @Transactional
    public List<CartItem> findItemsByCartId(Long cartId) {
        ShoppingCart cart = mapper.toDomain(cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado con ID: " + cartId)));
        return cart.getItems();
    }

    @Override
    @Transactional
    public void clearCartItems(Long cartId) {
        cartItemRepository.deleteByShoppingCart_Id(cartId);

        // Actualizar ultima actividad del carrito
        ShoppingCartEntity cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado con ID: " + cartId));
        cart.setLastActivity(LocalDateTime.now());
        cartRepository.save(cart);
    }
}