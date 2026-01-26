package com.sergejava.telegram_app.mapper;

import com.sergejava.telegram_app.dto.CategoryDTO;
import com.sergejava.telegram_app.entity.Category;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CategoryMapper {

    public static CategoryDTO toDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }

}
