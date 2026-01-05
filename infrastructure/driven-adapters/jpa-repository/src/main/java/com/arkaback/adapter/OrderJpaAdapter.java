package com.arkaback.adapter;

import com.arkaback.entity.Order;
import com.arkaback.entity.OrderEntity;
import com.arkaback.mappers.OrderPersistenceMapper;
import com.arkaback.ports.output.OrderPersistencePort;
import com.arkaback.repository.OrderJpaRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class OrderJpaAdapter implements OrderPersistencePort {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderPersistenceMapper mapper;

    @Override
    @Transactional
    public Order save(Order order) {
        OrderEntity entity = mapper.toEntity(order);
        OrderEntity saved = orderJpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional
    public Optional<Order> findById(Long id) {
        return orderJpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional
    public List<Order> findAll() {
        return orderJpaRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public List<Order> findByPersonId(Long personId) {
        return orderJpaRepository.findByPerson_Id(personId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public String generateOrderCode() {
        // Formato: ORD-YYYYMMDD-XXXXX
        // Ejemplo: ORD-20250102-A3F2E
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uniquePart = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        String orderCode = "ORD-" + datePart + "-" + uniquePart;

        // Verificamos que no exista
        while (orderJpaRepository.existsByOrderCode(orderCode)) {
            uniquePart = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
            orderCode = "ORD-" + datePart + "-" + uniquePart;
        }

        return orderCode;
    }
}
