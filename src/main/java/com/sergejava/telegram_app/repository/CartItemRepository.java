package com.sergejava.telegram_app.repository;

import com.sergejava.telegram_app.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
