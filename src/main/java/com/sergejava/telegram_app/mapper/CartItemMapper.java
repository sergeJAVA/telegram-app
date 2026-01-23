package com.sergejava.telegram_app.mapper;

import com.sergejava.telegram_app.dto.CartItemDTO;
import com.sergejava.telegram_app.entity.CartItem;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CartItemMapper {

    public static CartItemDTO toDTO(CartItem cartItem) {
        return CartItemDTO.builder()
                .id(cartItem.getId())
                .quantity(cartItem.getQuantity())
                .price(cartItem.getPrice())
                .productId(cartItem.getProduct().getId())
                .productSize(cartItem.getProductSize())
                .build();
    }

}
