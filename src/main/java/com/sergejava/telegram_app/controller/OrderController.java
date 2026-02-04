package com.sergejava.telegram_app.controller;

import com.sergejava.telegram_app.dto.CreateOrderRequest;
import com.sergejava.telegram_app.dto.OrderDTO;
import com.sergejava.telegram_app.security.TokenAuthentication;
import com.sergejava.telegram_app.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody @Valid CreateOrderRequest request,
                                         Authentication authentication) {
        TokenAuthentication tokenAuthentication = (TokenAuthentication) authentication;
        OrderDTO response = orderService.createOrderFromCart(tokenAuthentication.getTokenData(), request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        OrderDTO orderDTO = orderService.cancelOrder(id);
        return ResponseEntity.ok(orderDTO);
    }

}
