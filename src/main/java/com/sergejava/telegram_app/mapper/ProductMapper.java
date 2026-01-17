package com.sergejava.telegram_app.mapper;

import com.sergejava.telegram_app.dto.ProductDTO;
import com.sergejava.telegram_app.entity.Product;
import lombok.experimental.UtilityClass;

import java.util.stream.Collectors;

@UtilityClass
public class ProductMapper {

    public static ProductDTO toDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .stock(product.getStock())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .sizes(product.getProductSizes().stream()
                        .map(ProductSizeMapper::toDTO)
                        .collect(Collectors.toSet())
                )
                .build();
    }

}
