package com.sergejava.telegram_app.exceptions;

public class InvalidImageUrlException extends RuntimeException {

    public InvalidImageUrlException() {
        super("One of the image URLs is invalid!");
    }

}
