package com.sergejava.telegram_app.exceptions;

public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(Long categoryId) {
        super(String.format("Category with ID '%d' not found!", categoryId));
    }

}
