package com.arkaback.repository;

import com.arkaback.entity.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryJpaRepository extends JpaRepository<InventoryEntity, Long> {
    Optional<InventoryEntity> findByProduct_IdAndWarehouse_Id(Long productId, Long warehouseId);

}
