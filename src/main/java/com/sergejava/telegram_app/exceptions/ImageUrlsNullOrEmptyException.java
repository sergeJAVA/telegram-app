package com.sergejava.telegram_app.exceptions;

public class ImageUrlsNullOrEmptyException extends RuntimeException {

    public ImageUrlsNullOrEmptyException() {
        super("The URLs for the images are empty or null. Please provide a link to them.");
    }
}
