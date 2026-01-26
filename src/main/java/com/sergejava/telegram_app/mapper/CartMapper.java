package com.sergejava.telegram_app.mapper;

import com.sergejava.telegram_app.dto.CartDTO;
import com.sergejava.telegram_app.entity.Cart;
import lombok.experimental.UtilityClass;

import java.util.stream.Collectors;

@UtilityClass
public class CartMapper {

    public static CartDTO toDTO(Cart cart) {
        return CartDTO.builder()
                .id(cart.getId())
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .userTelegramId(cart.getUser().getUserId())
                .cartItems(cart.getCartItems().stream()
                        .map(CartItemMapper::toDTO)
                        .collect(Collectors.toSet()))
                .build();
    }

}
