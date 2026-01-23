package com.sergejava.telegram_app.repository;

import com.sergejava.telegram_app.entity.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @Query("select p from Product p where p.id = :id")
    @EntityGraph(attributePaths = {
            "category",
            "productSizes",
            "productSizes.size",
            "images"
    })
    Optional<Product> findProduct(@Param("id") Long id);

}
