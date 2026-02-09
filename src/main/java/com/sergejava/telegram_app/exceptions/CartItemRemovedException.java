package com.sergejava.telegram_app.exceptions;

public class CartItemRemovedException extends RuntimeException {

    public CartItemRemovedException(String message) {
        super(message);
    }

    public CartItemRemovedException(Long itemId) {
        super(String.format("CartItem with ID '%d' has been removed!", itemId));
    }

}
