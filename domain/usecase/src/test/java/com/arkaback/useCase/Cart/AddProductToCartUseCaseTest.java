package com.arkaback.useCase.Cart;

import com.arkaback.entity.cart.CartItem;
import com.arkaback.entity.cart.CartStatus;
import com.arkaback.entity.cart.ShoppingCart;
import com.arkaback.entity.category.Category;
import com.arkaback.entity.inventory.Inventory;
import com.arkaback.entity.person.Person;
import com.arkaback.entity.product.Product;
import com.arkaback.exceptions.domain.InsufficientStockException;
import com.arkaback.exceptions.infrastructure.ProductNotFoundException;
import com.arkaback.ports.output.InventoryPersistencePort;
import com.arkaback.ports.output.ProductPersistencePort;
import com.arkaback.ports.output.ShoppingCartPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests Unitarios para AddProductToCartUseCase
 *
 * Casos críticos:
 * - Agregar producto nuevo al carrito
 * - SUMAR cantidades cuando producto ya existe
 * - Validar stock disponible
 * - Crear carrito si no existe
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AddProductToCartUseCase - Tests Unitarios")
class AddProductToCartUseCaseTest {

    @Mock
    private ShoppingCartPersistencePort cartPersistencePort;

    @Mock
    private ProductPersistencePort productPersistencePort;

    @Mock
    private InventoryPersistencePort inventoryPersistencePort;

    @InjectMocks
    private AddProductToCartUseCase addProductToCartUseCase;

    private Person person;
    private Product product;
    private ShoppingCart activeCart;
    private Inventory inventory;

    @BeforeEach
    void setUp() {
        person = Person.builder()
                .id(1L)
                .name("Juan Pérez")
                .email("juan@example.com")
                .build();

        Category category = Category.builder()
                .id(1L)
                .name("Periféricos")
                .build();

        product = Product.builder()
                .id(1L)
                .name("Mouse Logitech")
                .sku("MOUSE-001")
                .price(new BigDecimal("25.99"))
                .category(category)
                .build();

        activeCart = ShoppingCart.builder()
                .id(1L)
                .person(person)
                .status(CartStatus.ACTIVE)
                .items(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .lastActivity(LocalDateTime.now())
                .build();

        inventory = Inventory.builder()
                .id(1L)
                .product(product)
                .stockActual(100)
                .stockReserved(0)
                .build();
    }

    @Test
    @DisplayName("Debería agregar producto NUEVO al carrito exitosamente")
    void shouldAddNewProductToCart() {
        // Given
        when(cartPersistencePort.findActiveCartByPersonId(1L))
                .thenReturn(Optional.of(activeCart));
        when(productPersistencePort.findById(1L))
                .thenReturn(Optional.of(product));
        when(inventoryPersistencePort.findByProductIdAndWarehouseId(1L, 1L))
                .thenReturn(Optional.of(inventory));
        when(cartPersistencePort.findCartItemByCartAndProduct(1L, 1L))
                .thenReturn(Optional.empty()); // NO existe
        when(cartPersistencePort.findById(1L))
                .thenReturn(Optional.of(activeCart));

        // When
        ShoppingCart result = addProductToCartUseCase.execute(1L, 1L, 3);

        // Then
        assertNotNull(result);
        verify(cartPersistencePort).addCartItem(any(CartItem.class));
        verify(cartPersistencePort).save(any(ShoppingCart.class));
    }

    @Test
    @DisplayName("Debería SUMAR cantidades cuando producto ya existe en carrito")
    void shouldSumQuantitiesWhenProductExists() {
        // Given - Producto ya tiene cantidad 2 en carrito
        CartItem existingItem = CartItem.builder()
                .id(10L)
                .product(product)
                .quantity(2)  // Ya tiene 2
                .unitPrice(new BigDecimal("25.99"))
                .build();

        when(cartPersistencePort.findActiveCartByPersonId(1L))
                .thenReturn(Optional.of(activeCart));
        when(productPersistencePort.findById(1L))
                .thenReturn(Optional.of(product));
        when(inventoryPersistencePort.findByProductIdAndWarehouseId(1L, 1L))
                .thenReturn(Optional.of(inventory));
        when(cartPersistencePort.findCartItemByCartAndProduct(1L, 1L))
                .thenReturn(Optional.of(existingItem)); // YA EXISTE
        when(cartPersistencePort.findById(1L))
                .thenReturn(Optional.of(activeCart));

        // When - Agregar 3 más
        ShoppingCart result = addProductToCartUseCase.execute(1L, 1L, 3);

        // Then - Debe quedar con 5 (2 + 3)
        verify(cartPersistencePort).updateCartItem(argThat(item ->
                item.getQuantity() == 5  // ✅ SUMA: 2 + 3 = 5
        ));
    }

    @Test
    @DisplayName("Debería crear carrito si no existe")
    void shouldCreateCartIfNotExists() {
        // Given - No hay carrito activo
        when(cartPersistencePort.findActiveCartByPersonId(1L))
                .thenReturn(Optional.empty()); // NO EXISTE
        when(cartPersistencePort.createCart(1L))
                .thenReturn(activeCart);
        when(productPersistencePort.findById(1L))
                .thenReturn(Optional.of(product));
        when(inventoryPersistencePort.findByProductIdAndWarehouseId(1L, 1L))
                .thenReturn(Optional.of(inventory));
        when(cartPersistencePort.findCartItemByCartAndProduct(1L, 1L))
                .thenReturn(Optional.empty());
        when(cartPersistencePort.findById(1L))
                .thenReturn(Optional.of(activeCart));

        // When
        ShoppingCart result = addProductToCartUseCase.execute(1L, 1L, 2);

        // Then
        verify(cartPersistencePort).createCart(1L);
        assertNotNull(result);
    }

    @Test
    @DisplayName("Debería lanzar InsufficientStockException cuando no hay stock disponible")
    void shouldThrowExceptionWhenInsufficientStock() {
        // Given - Stock: 100 actual, 98 reservado = 2 disponibles
        Inventory lowInventory = Inventory.builder()
                .stockActual(100)
                .stockReserved(98)  // Solo 2 disponibles
                .build();

        when(cartPersistencePort.findActiveCartByPersonId(1L))
                .thenReturn(Optional.of(activeCart));
        when(productPersistencePort.findById(1L))
                .thenReturn(Optional.of(product));
        when(inventoryPersistencePort.findByProductIdAndWarehouseId(1L, 1L))
                .thenReturn(Optional.of(lowInventory));
        when(cartPersistencePort.findCartItemByCartAndProduct(1L, 1L))
                .thenReturn(Optional.empty());

        // When & Then - Intentar agregar 5 (más de los 2 disponibles)
        InsufficientStockException exception = assertThrows(
                InsufficientStockException.class,
                () -> addProductToCartUseCase.execute(1L, 1L, 5)
        );

        assertTrue(exception.getMessage().contains("Disponible: 2"));
        assertTrue(exception.getMessage().contains("Solicitado: 5"));
    }

    @Test
    @DisplayName("Debería validar stock para cantidad TOTAL cuando producto ya existe")
    void shouldValidateTotalQuantityWhenProductExists() {
        // Given - Producto ya tiene 3 unidades en carrito
        CartItem existingItem = CartItem.builder()
                .id(10L)
                .product(product)
                .quantity(3)  // Ya tiene 3
                .unitPrice(new BigDecimal("25.99"))
                .build();

        // Stock: 100 actual, 95 reservado = 5 disponibles
        Inventory limitedInventory = Inventory.builder()
                .stockActual(100)
                .stockReserved(95)  // Solo 5 disponibles
                .build();

        when(cartPersistencePort.findActiveCartByPersonId(1L))
                .thenReturn(Optional.of(activeCart));
        when(productPersistencePort.findById(1L))
                .thenReturn(Optional.of(product));
        when(inventoryPersistencePort.findByProductIdAndWarehouseId(1L, 1L))
                .thenReturn(Optional.of(limitedInventory));
        when(cartPersistencePort.findCartItemByCartAndProduct(1L, 1L))
                .thenReturn(Optional.of(existingItem));

        // When & Then - Intentar agregar 3 más (total 6, pero solo hay 5)
        InsufficientStockException exception = assertThrows(
                InsufficientStockException.class,
                () -> addProductToCartUseCase.execute(1L, 1L, 3)
        );

        assertTrue(exception.getMessage().contains("En carrito: 3"));
        assertTrue(exception.getMessage().contains("Intentando agregar: 3"));
    }

    @Test
    @DisplayName("Debería lanzar ProductNotFoundException cuando producto no existe")
    void shouldThrowExceptionWhenProductNotFound() {
        // Given
        when(cartPersistencePort.findActiveCartByPersonId(1L))
                .thenReturn(Optional.of(activeCart));
        when(productPersistencePort.findById(999L))
                .thenReturn(Optional.empty()); // NO EXISTE

        // When & Then
        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> addProductToCartUseCase.execute(1L, 999L, 2)
        );

        assertTrue(exception.getMessage().contains("999"));
    }

    @Test
    @DisplayName("Debería lanzar IllegalArgumentException con cantidad cero")
    void shouldThrowExceptionWhenQuantityIsZero() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> addProductToCartUseCase.execute(1L, 1L, 0)
        );

        assertTrue(exception.getMessage().contains("mayor a 0"));
    }

    @Test
    @DisplayName("Debería lanzar IllegalArgumentException con cantidad negativa")
    void shouldThrowExceptionWhenQuantityIsNegative() {
        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> addProductToCartUseCase.execute(1L, 1L, -5));
    }
}