package com.sergejava.telegram_app.repository;

import com.sergejava.telegram_app.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    @Query("select o from Order o where o.id = :id")
    @EntityGraph(attributePaths = {
            "user",
            "user.cart",
            "orderItems",
            "orderItems.product",
            "orderItems.product.productSizes",
            "orderItems.product.productSizes.size"
    })
    Optional<Order> findByIdWithAllLinks(@Param("id") Long id);

    @EntityGraph(attributePaths = {"orderItems"})
    Page<Order> findAll(Specification<Order> spec, Pageable pageable);

}
