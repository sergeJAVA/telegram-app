package com.sergejava.telegram_app.service.impl;

import com.sergejava.telegram_app.dto.CategoryDTO;
import com.sergejava.telegram_app.entity.Category;
import com.sergejava.telegram_app.exceptions.CategoryAlreadyExistsException;
import com.sergejava.telegram_app.mapper.CategoryMapper;
import com.sergejava.telegram_app.repository.CategoryRepository;
import com.sergejava.telegram_app.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        categoryRepository.findByName(categoryDTO.getName()).ifPresent(category -> {
            throw new CategoryAlreadyExistsException(category.getName());
        });
        Category category = Category.builder()
                .name(categoryDTO.getName())
                .description(categoryDTO.getDescription())
                .build();
        return CategoryMapper.toDTO(categoryRepository.save(category));
    }

}
