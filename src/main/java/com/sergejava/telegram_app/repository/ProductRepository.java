package com.sergejava.telegram_app.repository;

import com.sergejava.telegram_app.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
