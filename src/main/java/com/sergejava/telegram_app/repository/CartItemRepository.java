package com.sergejava.telegram_app.repository;

import com.sergejava.telegram_app.entity.CartItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Query("SELECT ci FROM CartItem ci " +
           "WHERE ci.product.name = :productName " +
           "AND ci.productSize.size.name = :sizeName " +
           "AND ci.cart.id = :cartId")
    @EntityGraph(attributePaths = {"cart", "product", "productSize", "productSize.size", "cart.user"})
    Optional<CartItem> findByProductAndSizeAndCart(@Param("productName") String productName,
                                                   @Param("sizeName") String sizeName,
                                                   @Param("cartId") Long cartId);

}
