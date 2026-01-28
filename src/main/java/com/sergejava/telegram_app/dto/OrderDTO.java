package com.sergejava.telegram_app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDTO {

    private Long id;

    @JsonProperty("total_price")
    private BigDecimal totalPrice;

    private String status;

    @JsonProperty("delivery_address")
    private String deliveryAddress;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @Builder.Default
    @JsonProperty("order_items")
    private List<OrderItemDTO> orderItems = new ArrayList<>();

}
