package com.sergejava.telegram_app.exceptions;

public class UserIdNotEqualsToAdminIdException extends RuntimeException {

    public UserIdNotEqualsToAdminIdException(String message) {
        super(message);
    }

}
