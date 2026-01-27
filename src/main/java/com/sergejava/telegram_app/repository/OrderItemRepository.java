package com.sergejava.telegram_app.repository;

import com.sergejava.telegram_app.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
