package com.sergejava.telegram_app.service;

import com.sergejava.telegram_app.dto.CartDTO;

public interface CartService {

    CartDTO getCartByUserId(Long userId);

}
