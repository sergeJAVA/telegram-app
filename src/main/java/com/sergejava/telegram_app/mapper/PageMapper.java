package com.sergejava.telegram_app.mapper;

import com.sergejava.telegram_app.dto.PageDTO;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;

@UtilityClass
public class PageMapper {

    public static <T> PageDTO<T> toDTO(Page<T> page) {
        return PageDTO.<T>builder()
                .content(page.getContent())
                .size(page.getSize())
                .number(page.getNumber())
                .first(page.isFirst())
                .last(page.isLast())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .numberOfElements(page.getNumberOfElements())
                .build();
    }

}
