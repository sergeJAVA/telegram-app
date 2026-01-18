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
public class ProductImageDTO {

    private Long id;

    private String url;

    @JsonProperty("is_main")
    @Builder.Default
    private Boolean isMain = false;

    @JsonProperty("product_id")
    private Long productId;

}
