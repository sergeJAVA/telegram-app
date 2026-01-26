package com.sergejava.telegram_app.service;

import com.sergejava.telegram_app.dto.AddItemToCartRequest;
import com.sergejava.telegram_app.dto.CartItemDTO;
import com.sergejava.telegram_app.security.TokenData;

public interface CartItemService {

    CartItemDTO addItemToCart(TokenData tokenData, AddItemToCartRequest request);

}
