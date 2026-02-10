package com.sergejava.telegram_app.exceptions;

public class CartItemNotFoundException extends RuntimeException {

    public CartItemNotFoundException(String message) {
        super(message);
    }

    public CartItemNotFoundException(Long itemId) {
        super(String.format("CartItem with ID '%d' not found!", itemId));
    }

}
