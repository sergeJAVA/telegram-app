package com.sergejava.telegram_app.mapper;

import com.sergejava.telegram_app.dto.ProductSizeDTO;
import com.sergejava.telegram_app.entity.ProductSize;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ProductSizeMapper {

    public static ProductSizeDTO toDTO(ProductSize productSize) {
        return ProductSizeDTO.builder()
                .id(productSize.getId())
                .stock(productSize.getStock())
                .productId(productSize.getProduct().getId())
                .sizeId(productSize.getSize().getId())
                .build();
    }

}
