package com.sergejava.telegram_app.controller;

import com.sergejava.telegram_app.dto.AddItemToCartRequest;
import com.sergejava.telegram_app.dto.CartItemDTO;
import com.sergejava.telegram_app.dto.ChangeItemQuantityRequest;
import com.sergejava.telegram_app.security.TokenAuthentication;
import com.sergejava.telegram_app.security.TokenData;
import com.sergejava.telegram_app.service.CartItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

    @PostMapping
    public ResponseEntity<CartItemDTO> addItemToCart(@RequestBody AddItemToCartRequest request, Authentication authentication) {
        TokenAuthentication tokenAuthentication = (TokenAuthentication) authentication;
        TokenData tokenData = tokenAuthentication.getTokenData();
        return ResponseEntity.ok(service.addItemToCart(tokenData, request));
    }

    @PatchMapping("/reduce")
    public ResponseEntity<?> reduceItemQuantity(@Valid @RequestBody ChangeItemQuantityRequest request) {
        CartItemDTO cartItemDTO = service.reduceItemQuantity(request.getItemId(), request.getQuantity());
        return ResponseEntity.ok(cartItemDTO);
    }

    @PatchMapping("/increase")
    public ResponseEntity<?> increaseItemQuantity(@Valid @RequestBody ChangeItemQuantityRequest request) {
        CartItemDTO cartItemDTO = service.increaseItemQuantity(request.getItemId(), request.getQuantity());
        return ResponseEntity.ok(cartItemDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItemById(@PathVariable("id") Long id) {
        service.deleteItemById(id);
        return ResponseEntity.ok(null);
    }

}
