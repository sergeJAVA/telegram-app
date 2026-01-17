package com.sergejava.telegram_app.exceptions;

public class SizeNotFoundByNameException extends RuntimeException {

    public SizeNotFoundByNameException(String sizeName) {
        super(String.format("Size with name '%s' not found!", sizeName));
    }

}
