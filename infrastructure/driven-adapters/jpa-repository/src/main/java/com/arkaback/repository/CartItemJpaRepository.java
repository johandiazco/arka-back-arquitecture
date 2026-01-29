package com.arkaback.repository;

import com.arkaback.entity.cart.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface CartItemJpaRepository extends JpaRepository<CartItemEntity, Long> {
    Optional<CartItemEntity> findByShoppingCart_IdAndProduct_Id(Long cartId, Long productId);
    List<CartItemEntity> findByShoppingCart_Id(Long cartId);
    //Elimina todos los productos de un carrito
    @Modifying
    @Query("DELETE FROM CartItemEntity c WHERE c.shoppingCart.id = :cartId")
    void deleteByShoppingCart_Id(@Param("cartId") Long cartId);
    boolean existsByShoppingCart_IdAndProduct_Id(Long cartId, Long productId);
}
