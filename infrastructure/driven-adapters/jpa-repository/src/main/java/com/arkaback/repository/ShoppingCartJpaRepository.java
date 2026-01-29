package com.arkaback.repository;

import com.arkaback.entity.cart.CartStatusEntity;
import com.arkaback.entity.cart.ShoppingCartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ShoppingCartJpaRepository extends JpaRepository<ShoppingCartEntity, Long> {

    //Buscamos carrito activo de un cliente donde solo puede tener 1 carrito activo a la vez
    Optional<ShoppingCartEntity> findByPerson_IdAndStatus(Long personId, CartStatusEntity status);

    //Lista los carritos por estado
    List<ShoppingCartEntity> findByStatus(CartStatusEntity status);

    //Lista carritos abandonados con Estado = activo y ultima actividad < thresholdDate
    @Query("SELECT c FROM ShoppingCartEntity c " +
            "WHERE c.status = 'ACTIVE' " +
            "AND c.lastActivity < :thresholdDate")
    List<ShoppingCartEntity> findAbandonedCarts(@Param("thresholdDate") LocalDateTime thresholdDate);

    //Verifica si existe un carrito activo para un cliente
    boolean existsByPerson_IdAndStatus(Long personId, CartStatusEntity status);
}













