package com.sergejava.telegram_app.service;

import com.sergejava.telegram_app.dto.CategoryDTO;
import com.sergejava.telegram_app.entity.Category;

import java.util.List;

/**
 * Интерфейс, предоставляющий методы, для работы с бизнес-логикой над сущностью {@link Category}.
 *
 * @author sergeJAVA
 */
public interface CategoryService {

    /**
     * Метод для создания новой категории в БД.
     *
     * @param categoryDTO
     * @return {@link CategoryDTO}
     *
     * @author sergeJAVA
     */
    CategoryDTO createCategory(CategoryDTO categoryDTO);

    /**
     * Метод для получения всех категорий.
     *
     * @return {@code List<CategoryDTO>}
     *
     * @author sergeJAVA
     */
    List<CategoryDTO> findAll();

}
