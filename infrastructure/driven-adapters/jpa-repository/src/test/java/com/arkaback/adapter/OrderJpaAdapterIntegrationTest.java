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
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = OrderJpaAdapterIntegrationTest.TestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class OrderJpaAdapterIntegrationTest {

    @Configuration
    @EnableAutoConfiguration
    @EntityScan(basePackages = {
            "com.arkaback.entity.order",
            "com.arkaback.entity.person",
            "com.arkaback.entity.product",
            "com.arkaback.entity.category",
            "com.arkaback.entity.warehouse",
            "com.arkaback.entity.inventory",
            "com.arkaback.entity.supplier",
            "com.arkaback.entity.cart",
            "com.arkaback.entity.notification"
    })
    @EnableJpaRepositories(basePackages = "com.arkaback.repository")
    static class TestConfig {
        @Bean
        public OrderPersistenceMapper orderPersistenceMapper() {
            return new OrderPersistenceMapper();
        }

        @Bean
        public OrderJpaAdapter orderJpaAdapter(
                OrderJpaRepository orderJpaRepository,
                OrderPersistenceMapper mapper) {
            return new OrderJpaAdapter(orderJpaRepository, mapper);
        }
    }

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.show-sql", () -> "true");
        registry.add("spring.mail.host", () -> "localhost");
        registry.add("spring.mail.port", () -> "1025");
        registry.add("spring.jpa.mapping-resources", () -> "");
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
    private OrderJpaAdapter orderAdapter;

    private PersonEntity personEntity;
    private ProductEntity productEntity;
    private WarehouseEntity warehouseEntity;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
        personRepository.deleteAll();
        warehouseRepository.deleteAll();

        personEntity = personRepository.save(PersonEntity.builder()
                .name("Juan Pérez")
                .email("juan@test.com")
                .passwordHash("hash123")
                .phone("555-0100")
                .address("Calle Falsa 123")
                .isActive(true)
                .build());

        productEntity = productRepository.save(ProductEntity.builder()
                .name("Mouse Logitech")
                .description("Mouse gaming")
                .price(new BigDecimal("59.99"))
                .sku("MOUSE-TEST-001")
                .brand("Logitech")
                .minStock(5)
                .isActive(true)
                .category(CategoryEntity.builder()
                        .name("Periféricos")
                        .description("Periféricos de computadora")
                        .isActive(true)
                        .build())
                .build());

        warehouseEntity = warehouseRepository.save(WarehouseEntity.builder()
                .name("Bodega Principal")
                .country("Colombia")
                .city("Bogotá")
                .address("Cra 7 # 32-16")
                .phone("555-0200")
                .isActive(true)
                .build());
    }

    @Test
    @DisplayName("Debería persistir Order completo con detalles en BD")
    void shouldPersistOrderWithDetails() {
        Order order = Order.builder()
                .person(Person.builder().id(personEntity.getId())
                        .name(personEntity.getName()).email(personEntity.getEmail()).build())
                .warehouse(Warehouse.builder().id(warehouseEntity.getId())
                        .name(warehouseEntity.getName()).build())
                .details(List.of(OrderDetail.builder()
                        .product(Product.builder().id(productEntity.getId())
                                .name(productEntity.getName()).price(productEntity.getPrice()).build())
                        .quantity(3)
                        .unitPrice(new BigDecimal("59.99"))
                        .build()))
                .orderStatus(OrderStatu.PENDIENTE)
                .build();

        Order saved = orderAdapter.save(order);

        assertNotNull(saved.getId());
        assertNotNull(saved.getOrderCode());
        assertEquals(OrderStatu.PENDIENTE, saved.getOrderStatus());
        assertEquals(1, saved.getDetails().size());
        assertEquals(new BigDecimal("59.99").multiply(new BigDecimal("3")), saved.calculateTotal());
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
    }

    @Test
    @DisplayName("Debería encontrar órdenes por ID de persona")
    void shouldFindOrdersByPersonId() {
        Person person = Person.builder().id(personEntity.getId()).build();
        Warehouse warehouse = Warehouse.builder().id(warehouseEntity.getId()).build();
        Product product = Product.builder().id(productEntity.getId()).build();
        OrderDetail detail = OrderDetail.builder()
                .product(product).quantity(1).unitPrice(new BigDecimal("10.00")).build();

        orderAdapter.save(Order.builder().person(person).warehouse(warehouse)
                .details(List.of(detail)).orderStatus(OrderStatu.PENDIENTE).build());
        orderAdapter.save(Order.builder().person(person).warehouse(warehouse)
                .details(List.of(detail)).orderStatus(OrderStatu.CONFIRMADO).build());

        List<Order> orders = orderAdapter.findByPersonId(personEntity.getId());

        assertEquals(2, orders.size());
        assertTrue(orders.stream().allMatch(o ->
                o.getPerson().getId().equals(personEntity.getId())));
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
        Product product = Product.builder().id(productEntity.getId()).build();
        Person person = Person.builder().id(personEntity.getId()).build();
        Warehouse warehouse = Warehouse.builder().id(warehouseEntity.getId()).build();

        Order saved = orderAdapter.save(Order.builder()
                .person(person).warehouse(warehouse)
                .details(List.of(
                        OrderDetail.builder().product(product).quantity(2)
                                .unitPrice(new BigDecimal("10.00")).build(),
                        OrderDetail.builder().product(product).quantity(3)
                                .unitPrice(new BigDecimal("15.50")).build()
                ))
                .orderStatus(OrderStatu.PENDIENTE)
                .build());

        assertEquals(new BigDecimal("66.50"), saved.calculateTotal());
    }
}