package com.sergejava.telegram_app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductSizeDTO {

    private Long id;

    private Integer stock;

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("size_id")
    private Integer sizeId;

}
