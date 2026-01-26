package com.sergejava.telegram_app.mapper;

import com.sergejava.telegram_app.dto.ProductDTO;
import com.sergejava.telegram_app.dto.ProductImageDTO;
import com.sergejava.telegram_app.entity.Product;
import com.sergejava.telegram_app.entity.ProductImage;
import lombok.experimental.UtilityClass;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class ProductMapper {

    public static ProductDTO toDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .imageUrls(getImages(product))
                .stock(product.getStock())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .sizes(product.getProductSizes().stream()
                        .map(ProductSizeMapper::toDTO)
                        .collect(Collectors.toSet())
                )
                .build();
    }

    private Set<ProductImageDTO> getImages(Product product) {
        Set<ProductImageDTO> images = new LinkedHashSet<>();
        for (ProductImage image : product.getImages()) {
            images.add(ProductImageMapper.toDTO(image));
        }
        return images;
    }

}
