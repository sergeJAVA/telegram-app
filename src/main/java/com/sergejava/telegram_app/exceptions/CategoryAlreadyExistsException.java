package com.sergejava.telegram_app.exceptions;

public class CategoryAlreadyExistsException extends RuntimeException {

    public CategoryAlreadyExistsException(String categoryName) {
        super(String.format("Category with name '%s' already exists!", categoryName));
    }

}
