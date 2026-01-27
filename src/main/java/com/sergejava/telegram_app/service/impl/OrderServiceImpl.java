package com.sergejava.telegram_app.service.impl;

import com.sergejava.telegram_app.dto.CreateOrderRequest;
import com.sergejava.telegram_app.dto.OrderDTO;
import com.sergejava.telegram_app.security.TokenData;
import com.sergejava.telegram_app.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    @Override
    public OrderDTO createOrderFromCart(TokenData tokenData, CreateOrderRequest request) {
//      TODO: реализовать метод для создания Order и OrderItem's на основе корзины и request.
//       Найти корзину через поле userTelegramId, которое хранится в tokenData.
//       Если корзина пустая, то вернуть какой-нибудь exception, иначе создавать Order и OrderItem на основе CartItem

        return null;
    }

}
