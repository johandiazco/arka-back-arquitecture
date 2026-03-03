package com.arkaback.adapter;

import com.arkaback.entity.category.CategoryEntity;
import com.arkaback.entity.order.Order;
import com.arkaback.entity.order.OrderDetail;
import com.arkaback.entity.order.OrderStatu;
import com.arkaback.entity.person.Person;
import com.arkaback.entity.person.PersonEntity;
import com.arkaback.entity.product.Product;
import com.arkaback.entity.product.ProductEntity;
import com.arkaback.entity.warehouse.Warehouse;
import com.arkaback.entity.warehouse.WarehouseEntity;
import com.arkaback.mappers.OrderPersistenceMapper;
import com.arkaback.repository.OrderJpaRepository;
import com.arkaback.repository.PersonJpaRepository;
import com.arkaback.repository.ProductJpaRepository;
import com.arkaback.repository.WarehouseJpaRepository;
import net.bytebuddy.utility.dispatcher.JavaDispatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test de Integración para OrderJpaAdapter
 *
 * Usa Testcontainers para levantar MySQL real
 * Verifica:
 * - Persistencia completa de Order + OrderDetails
 * - Generación de código único
 * - Búsqueda por persona
 * - Integridad referencial
 */
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(OrderPersistenceMapper.class)
@DisplayName("OrderJpaAdapter - Tests de Integración")
class OrderJpaAdapterIntegrationTest {

    @JavaDispatcher.Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @Autowired
    private OrderJpaRepository orderRepository;

    @Autowired
    private PersonJpaRepository personRepository;

    @Autowired
    private ProductJpaRepository productRepository;

    @Autowired
    private WarehouseJpaRepository warehouseRepository;

    @Autowired
    private OrderPersistenceMapper mapper;

    private OrderJpaAdapter orderAdapter;
    private PersonEntity personEntity;
    private ProductEntity productEntity;
    private WarehouseEntity warehouseEntity;

    @BeforeEach
    void setUp() {
        orderAdapter = new OrderJpaAdapter(orderRepository, mapper);

        // Crear datos de prueba
        CategoryEntity category = CategoryEntity.builder()
                .name("Periféricos")
                .description("Periféricos de computadora")
                .isActive(true)
                .build();

        personEntity = PersonEntity.builder()
                .name("Juan Pérez")
                .email("juan@test.com")
                .passwordHash("hash123")
                .phone("555-0100")
                .address("Calle Falsa 123")
                .isActive(true)
                .build();
        personEntity = personRepository.save(personEntity);

        productEntity = ProductEntity.builder()
                .name("Mouse Logitech")
                .description("Mouse gaming")
                .price(new BigDecimal("59.99"))
                .sku("MOUSE-TEST-001")
                .brand("Logitech")
                .minStock(5)
                .isActive(true)
                .category(category)
                .build();
        productEntity = productRepository.save(productEntity);

        warehouseEntity = WarehouseEntity.builder()
                .name("Bodega Principal")
                .country("Colombia")
                .city("Bogotá")
                .address("Cra 7 # 32-16")
                .phone("555-0200")
                .isActive(true)
                .build();
        warehouseEntity = warehouseRepository.save(warehouseEntity);
    }

    @Test
    @DisplayName("Debería persistir Order completo con detalles en BD")
    void shouldPersistOrderWithDetails() {

        Person person = Person.builder()
                .id(personEntity.getId())
                .name(personEntity.getName())
                .email(personEntity.getEmail())
                .build();

        Product product = Product.builder()
                .id(productEntity.getId())
                .name(productEntity.getName())
                .price(productEntity.getPrice())
                .build();

        Warehouse warehouse = Warehouse.builder()
                .id(warehouseEntity.getId())
                .name(warehouseEntity.getName())
                .build();

        OrderDetail detail = OrderDetail.builder()
                .product(product)
                .quantity(3)
                .unitPrice(new BigDecimal("59.99"))
                .build();

        Order order = Order.builder()
                .person(person)
                .warehouse(warehouse)
                .details(List.of(detail))
                .orderStatus(OrderStatu.PENDIENTE)
                .build();

        Order savedOrder = orderAdapter.save(order);

        assertNotNull(savedOrder.getId());
        assertNotNull(savedOrder.getOrderCode());
        assertEquals(OrderStatu.PENDIENTE, savedOrder.getOrderStatus());
        assertEquals(1, savedOrder.getDetails().size());

        OrderDetail savedDetail = savedOrder.getDetails().get(0);
        assertEquals(3, savedDetail.getQuantity());
        assertEquals(new BigDecimal("59.99"), savedDetail.getUnitPrice());

        // Calcular total
        BigDecimal expectedTotal = new BigDecimal("59.99").multiply(new BigDecimal("3"));
        assertEquals(expectedTotal, savedOrder.calculateTotal());
    }

    @Test
    @DisplayName("Debería generar código único para cada orden")
    void shouldGenerateUniqueOrderCode() {

        String code1 = orderAdapter.generateOrderCode();
        String code2 = orderAdapter.generateOrderCode();

        assertNotNull(code1);
        assertNotNull(code2);
        assertNotEquals(code1, code2);
        assertTrue(code1.startsWith("ORD-"));
        assertTrue(code2.startsWith("ORD-"));
    }

    @Test
    @DisplayName("Debería encontrar órdenes por ID de persona")
    void shouldFindOrdersByPersonId() {
        // Crear 2 órdenes para la misma persona
        Person person = Person.builder()
                .id(personEntity.getId())
                .build();

        Warehouse warehouse = Warehouse.builder()
                .id(warehouseEntity.getId())
                .build();

        Product product = Product.builder()
                .id(productEntity.getId())
                .build();

        OrderDetail detail = OrderDetail.builder()
                .product(product)
                .quantity(1)
                .unitPrice(new BigDecimal("10.00"))
                .build();

        Order order1 = Order.builder()
                .person(person)
                .warehouse(warehouse)
                .details(List.of(detail))
                .orderStatus(OrderStatu.PENDIENTE)
                .build();

        Order order2 = Order.builder()
                .person(person)
                .warehouse(warehouse)
                .details(List.of(detail))
                .orderStatus(OrderStatu.CONFIRMADO)
                .build();

        orderAdapter.save(order1);
        orderAdapter.save(order2);

        List<Order> orders = orderAdapter.findByPersonId(personEntity.getId());

        assertNotNull(orders);
        assertEquals(2, orders.size());
        assertTrue(orders.stream().allMatch(o ->
                o.getPerson().getId().equals(personEntity.getId())
        ));
    }

    @Test
    @DisplayName("Debería retornar Optional.empty cuando orden no existe")
    void shouldReturnEmptyWhenOrderNotFound() {

        Optional<Order> result = orderAdapter.findById(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Debería calcular total correctamente con múltiples detalles")
    void shouldCalculateTotalWithMultipleDetails() {

        Person person = Person.builder().id(personEntity.getId()).build();
        Warehouse warehouse = Warehouse.builder().id(warehouseEntity.getId()).build();
        Product product = Product.builder().id(productEntity.getId()).build();

        OrderDetail detail1 = OrderDetail.builder()
                .product(product)
                .quantity(2)
                .unitPrice(new BigDecimal("10.00"))
                .build();

        OrderDetail detail2 = OrderDetail.builder()
                .product(product)
                .quantity(3)
                .unitPrice(new BigDecimal("15.50"))
                .build();

        Order order = Order.builder()
                .person(person)
                .warehouse(warehouse)
                .details(List.of(detail1, detail2))
                .orderStatus(OrderStatu.PENDIENTE)
                .build();

        Order saved = orderAdapter.save(order);

        // (2 × 10.00) + (3 × 15.50) = 20.00 + 46.50 = 66.50
        BigDecimal expectedTotal = new BigDecimal("66.50");
        assertEquals(expectedTotal, saved.calculateTotal());
    }
}
