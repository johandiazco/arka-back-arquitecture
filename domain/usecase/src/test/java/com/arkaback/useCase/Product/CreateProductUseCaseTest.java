package com.arkaback.useCase.Product;

import com.arkaback.entity.category.Category;
import com.arkaback.entity.product.Product;
import com.arkaback.exceptions.ProductAlreadyExistsException;
import com.arkaback.exceptions.domain.InvalidPriceException;
import com.arkaback.ports.output.InventoryPersistencePort;
import com.arkaback.ports.output.ProductPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Tests Unitarios para CreateProductUseCase
 *
 * Cobertura:
 * - Creación exitosa de producto
 * - Validación de SKU duplicado
 * - Validación de precio inválido
 * - Creación de inventario inicial automática
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CreateProductUseCase - Tests Unitarios")
class CreateProductUseCaseTest {

    @Mock
    private ProductPersistencePort productPersistencePort;

    @Mock
    private InventoryPersistencePort inventoryPersistencePort;

    @InjectMocks
    private CreateProductUseCase createProductUseCase;

    private Product validProduct;
    private Category category;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .id(1L)
                .name("Periféricos")
                .description("Periféricos de computadora")
                .isActive(true)
                .build();

        validProduct = Product.builder()
                .name("Mouse Logitech G502")
                .description("Mouse gaming RGB")
                .price(new BigDecimal("79.99"))
                .sku("MOUSE-LOG-G502")
                .brand("Logitech")
                .minStock(10)
                .isActive(true)
                .category(category)
                .build();
    }

    @Test
    @DisplayName("Debería crear producto exitosamente cuando el SKU no existe")
    void shouldCreateProductSuccessfully() {
        // Given
        when(productPersistencePort.existsBySku("MOUSE-LOG-G502")).thenReturn(false);

        Product savedProduct = validProduct.toBuilder().id(1L).build();
        when(productPersistencePort.save(any(Product.class))).thenReturn(savedProduct);

        // When
        Product result = createProductUseCase.create(validProduct, 1L, 1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("MOUSE-LOG-G502", result.getSku());
        assertEquals(new BigDecimal("79.99"), result.getPrice());

        // Verificar que se creó inventario inicial
        verify(inventoryPersistencePort, times(1))
                .createInitial(savedProduct, 1L, 1L);

        verify(productPersistencePort, times(1)).save(validProduct);
    }

    @Test
    @DisplayName("Debería lanzar ProductAlreadyExistsException cuando el SKU ya existe")
    void shouldThrowExceptionWhenSkuExists() {
        // Given
        when(productPersistencePort.existsBySku("MOUSE-LOG-G502")).thenReturn(true);

        // When & Then
        ProductAlreadyExistsException exception = assertThrows(
                ProductAlreadyExistsException.class,
                () -> createProductUseCase.create(validProduct, 1L, 1L)
        );

        assertTrue(exception.getMessage().contains("MOUSE-LOG-G502"));

        // Verificar que NO se guardó el producto
        verify(productPersistencePort, never()).save(any());
        verify(inventoryPersistencePort, never()).createInitial(any(), anyLong(), anyLong());
    }

    @Test
    @DisplayName("Debería lanzar InvalidPriceException cuando el precio es negativo")
    void shouldThrowExceptionWhenPriceIsNegative() {
        // Given
        Product invalidProduct = validProduct.toBuilder()
                .price(new BigDecimal("-10.00"))
                .build();

        // When & Then
        assertThrows(InvalidPriceException.class,
                () -> createProductUseCase.create(invalidProduct, 1L, 1L));

        verify(productPersistencePort, never()).existsBySku(any());
        verify(productPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar InvalidPriceException cuando el precio es cero")
    void shouldThrowExceptionWhenPriceIsZero() {
        // Given
        Product invalidProduct = validProduct.toBuilder()
                .price(BigDecimal.ZERO)
                .build();

        // When & Then
        assertThrows(InvalidPriceException.class,
                () -> createProductUseCase.create(invalidProduct, 1L, 1L));
    }

    @Test
    @DisplayName("Debería lanzar IllegalArgumentException cuando el SKU está vacío")
    void shouldThrowExceptionWhenSkuIsEmpty() {
        // Given
        Product invalidProduct = validProduct.toBuilder()
                .sku("")
                .build();

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> createProductUseCase.create(invalidProduct, 1L, 1L));
    }

    @Test
    @DisplayName("Debería lanzar IllegalArgumentException cuando minStock es negativo")
    void shouldThrowExceptionWhenMinStockIsNegative() {
        // Given
        Product invalidProduct = validProduct.toBuilder()
                .minStock(-5)
                .build();

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> createProductUseCase.create(invalidProduct, 1L, 1L));
    }

    @Test
    @DisplayName("Debería crear producto con minStock cero")
    void shouldCreateProductWithZeroMinStock() {
        // Given
        Product productWithZeroMinStock = validProduct.toBuilder()
                .minStock(0)
                .build();

        when(productPersistencePort.existsBySku("MOUSE-LOG-G502")).thenReturn(false);
        when(productPersistencePort.save(any(Product.class)))
                .thenReturn(productWithZeroMinStock.toBuilder().id(1L).build());

        // When
        Product result = createProductUseCase.create(productWithZeroMinStock, 1L, 1L);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getMinStock());
    }
}
