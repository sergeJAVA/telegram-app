package com.sergejava.telegram_app.exceptions;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long userId) {
        super(String.format("User with 'user_id' %d doesn't exist! Please register in the system.", userId));
    }

}
