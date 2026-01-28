package com.sergejava.telegram_app.mapper;

import com.sergejava.telegram_app.dto.OrderItemDTO;
import com.sergejava.telegram_app.entity.OrderItem;
import lombok.experimental.UtilityClass;

@UtilityClass
public class OrderItemMapper {

    public static OrderItemDTO toDTO(OrderItem orderItem) {
        return OrderItemDTO.builder()
                .id(orderItem.getId())
                .quantity(orderItem.getQuantity())
                .price(orderItem.getPrice())
                .productName(orderItem.getProductName())
                .productSize(orderItem.getProductSize())
                .imageURL(orderItem.getImageURL())
                .orderId(orderItem.getOrder().getId())
                .productId(orderItem.getProduct().getId())
                .build();
    }

}
