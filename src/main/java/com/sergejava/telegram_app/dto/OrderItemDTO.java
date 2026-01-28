package com.sergejava.telegram_app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemDTO {

    private Long id;

    private Integer quantity;

    private BigDecimal price;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("product_size")
    private String productSize;

    @JsonProperty("image_url")
    private String imageURL;

    @JsonProperty("order_id")
    private Long orderId;

    @JsonProperty("product_id")
    private Long productId;

}
