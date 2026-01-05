package com.arkaback.repository;

import com.arkaback.entity.WarehouseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WarehouseJpaRepository extends JpaRepository<WarehouseEntity, Long> {

    List<WarehouseEntity> findByCountry(String country);
    List<WarehouseEntity> findByIsActiveTrue();
}
