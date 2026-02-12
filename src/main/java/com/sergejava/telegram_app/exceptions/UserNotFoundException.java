package com.sergejava.telegram_app.exceptions;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long userId) {
        super(String.format("User with 'user_id' %d doesn't exist! Please register in the system.", userId));
    }

    public UserNotFoundException(String message) {
        super(message);
    }

    public static UserNotFoundException defaultMessage(Long userId) {
        return new UserNotFoundException(String.format("User with 'user_id' %d doesn't exist!", userId));
    }

}
