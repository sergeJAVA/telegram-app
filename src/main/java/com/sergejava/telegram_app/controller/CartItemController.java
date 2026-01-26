package com.sergejava.telegram_app.controller;

import com.sergejava.telegram_app.dto.AddItemToCartRequest;
import com.sergejava.telegram_app.dto.CartItemDTO;
import com.sergejava.telegram_app.entity.Product;
import com.sergejava.telegram_app.entity.ProductSize;
import com.sergejava.telegram_app.repository.ProductRepository;
import com.sergejava.telegram_app.security.TokenAuthentication;
import com.sergejava.telegram_app.security.TokenData;
import com.sergejava.telegram_app.service.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cartItems")
@RequiredArgsConstructor
public class CartItemController {

    private final CartItemService service;
    private final ProductRepository productRepository;

    @PostMapping
    public ResponseEntity<CartItemDTO> addItemToCart(@RequestBody AddItemToCartRequest request, Authentication authentication) {
        TokenAuthentication tokenAuthentication = (TokenAuthentication) authentication;
        TokenData tokenData = tokenAuthentication.getTokenData();
        return ResponseEntity.ok(service.addItemToCart(tokenData, request));
    }

}
