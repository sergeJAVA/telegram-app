package com.sergejava.telegram_app.repository;

import com.sergejava.telegram_app.entity.Cart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("select c from Cart c where c.user.userId = :userId")
    @EntityGraph(attributePaths =
            {
                "user",
                "user.roles",
                "cartItems",
                "cartItems.product",
                "cartItems.productSize",
                "cartItems.productSize.size",
                "cartItems.product.images"
            }
    )
    Optional<Cart> findByUserId(@Param("userId") Long userId);

}
