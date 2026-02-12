package com.sergejava.telegram_app.controller;

import com.sergejava.telegram_app.dto.CreateOrderRequest;
import com.sergejava.telegram_app.dto.OrderChangeStatusRequest;
import com.sergejava.telegram_app.dto.OrderDTO;
import com.sergejava.telegram_app.security.TokenAuthentication;
import com.sergejava.telegram_app.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
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
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> createOrder(@RequestBody @Valid CreateOrderRequest request,
                                         Authentication authentication) {
        TokenAuthentication tokenAuthentication = (TokenAuthentication) authentication;
        OrderDTO response = orderService.createOrderFromCart(tokenAuthentication.getTokenData(), request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        OrderDTO response = orderService.cancelOrder(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> changeOrderStatus(@RequestBody @Valid OrderChangeStatusRequest request) {
        OrderDTO response = orderService.changeStatus(request.getId(), request.getStatus());
        return ResponseEntity.ok(response);
    }

    //TODO: добавить контроллер для отмены заказа, который принадлежит пользователю.

    //TODO: добавить контроллер для получения заказов, которые принадлежат пользователю, с определённым статусом.

}
