package com.sergejava.telegram_app.exceptions;

public class AdminNotSpecifiedException extends RuntimeException {

    public AdminNotSpecifiedException(String message) {
        super(message);
    }

    public AdminNotSpecifiedException() {
        super("The ADMIN is not specified in the system.");
    }

}
