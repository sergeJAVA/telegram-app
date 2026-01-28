package com.sergejava.telegram_app.mapper;

import com.sergejava.telegram_app.dto.OrderDTO;
import com.sergejava.telegram_app.entity.Order;
import lombok.experimental.UtilityClass;

@UtilityClass
public class OrderMapper {

    public static OrderDTO toDTO(Order order) {
        return OrderDTO.builder()
                .id(order.getId())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus().name())
                .deliveryAddress(order.getDeliveryAddress())
                .phoneNumber(order.getPhoneNumber())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .orderItems(order.getOrderItems()
                        .stream()
                        .map(OrderItemMapper::toDTO)
                        .toList()
                )
                .build();
    }

}
