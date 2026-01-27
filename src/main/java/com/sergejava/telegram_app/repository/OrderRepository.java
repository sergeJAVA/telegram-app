package com.sergejava.telegram_app.repository;

import com.sergejava.telegram_app.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
