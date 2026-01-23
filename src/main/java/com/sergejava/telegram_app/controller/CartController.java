package com.sergejava.telegram_app.controller;

import com.sergejava.telegram_app.dto.CartDTO;
import com.sergejava.telegram_app.security.TokenAuthentication;
import com.sergejava.telegram_app.security.TokenData;
import com.sergejava.telegram_app.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartDTO> getCart(Authentication authentication){
        TokenAuthentication tokenAuthentication = (TokenAuthentication) authentication;
        TokenData tokenData = tokenAuthentication.getTokenData();
        CartDTO cartDTO = cartService.getCartByUserId(tokenData.getUserTelegramId());
        return ResponseEntity.ok(cartDTO);
    }

}
