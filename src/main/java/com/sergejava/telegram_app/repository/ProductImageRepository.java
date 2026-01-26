package com.sergejava.telegram_app.repository;

import com.sergejava.telegram_app.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
}
