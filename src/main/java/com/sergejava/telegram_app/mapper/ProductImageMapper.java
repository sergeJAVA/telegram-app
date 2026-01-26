package com.sergejava.telegram_app.mapper;

import com.sergejava.telegram_app.dto.ProductImageDTO;
import com.sergejava.telegram_app.entity.ProductImage;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ProductImageMapper {

    public static ProductImageDTO toDTO(ProductImage productImage) {
        return ProductImageDTO.builder()
                .id(productImage.getId())
                .url(productImage.getUrl())
                .isMain(productImage.getIsMain())
                .productId(productImage.getProduct().getId())
                .build();
    }

}
