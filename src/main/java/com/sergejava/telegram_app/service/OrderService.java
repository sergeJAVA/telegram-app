package com.sergejava.telegram_app.service;

import com.sergejava.telegram_app.dto.CreateOrderRequest;
import com.sergejava.telegram_app.dto.OrderDTO;
import com.sergejava.telegram_app.security.TokenData;

public interface OrderService {

    OrderDTO createOrderFromCart(TokenData tokenData, CreateOrderRequest request);

}
