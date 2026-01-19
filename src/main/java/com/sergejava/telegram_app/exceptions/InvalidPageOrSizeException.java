package com.sergejava.telegram_app.exceptions;

public class InvalidPageOrSizeException extends RuntimeException {

    public InvalidPageOrSizeException(String message) {
        super(message);
    }

}
