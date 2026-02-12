package com.sergejava.telegram_app.service;

import com.sergejava.telegram_app.dto.CategoryDTO;
import com.sergejava.telegram_app.entity.Category;

import java.util.List;

/**
 * <b>Интерфейс, предоставляющий методы, для работы с бизнес-логикой над сущностью {@link Category}.</b>
 *
 * @author sergeJAVA
 */
public interface CategoryService {

    /**
     * <b>Метод для создания новой категории в БД.</b>
     * @param categoryDTO
     * @return {@link CategoryDTO}
     *
     * @author sergeJAVA
     */
    CategoryDTO createCategory(CategoryDTO categoryDTO);

    /**
     * <strong>Метод для получения всех категорий.</strong>
     *
     * @return {@code List<CategoryDTO>}
     *
     * @author sergeJAVA
     */
    List<CategoryDTO> findAll();

}
