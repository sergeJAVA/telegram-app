package com.sergejava.telegram_app.mapper;

import com.sergejava.telegram_app.dto.CartItemDTO;
import com.sergejava.telegram_app.entity.CartItem;
import com.sergejava.telegram_app.entity.ProductImage;
import lombok.experimental.UtilityClass;

import java.util.Set;

@UtilityClass
public class CartItemMapper {

    public static CartItemDTO toDTO(CartItem cartItem) {
        Set<ProductImage> images = cartItem.getProduct().getImages();
        return CartItemDTO.builder()
                .id(cartItem.getId())
                .productName(cartItem.getProduct().getName())
                .quantity(cartItem.getQuantity())
                .price(cartItem.getPrice())
                .productId(cartItem.getProduct().getId())
                .productSize(cartItem.getProductSize().getSize().getName())
                .mainImageURL(
                        cartItem.getProduct()
                        .getMainProductImage(images)
                        .getUrl())
                .build();
    }

}
