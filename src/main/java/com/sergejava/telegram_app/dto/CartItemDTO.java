package com.sergejava.telegram_app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {

    private Long id;

    private Integer quantity;

    private BigDecimal price;

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("main_image_url")
    private String mainImageURL;

    @JsonProperty("product_size")
    private String productSize;

}
