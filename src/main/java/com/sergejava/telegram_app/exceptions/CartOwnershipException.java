package com.sergejava.telegram_app.exceptions;

public class CartOwnershipException extends RuntimeException {

    public CartOwnershipException(String message) {
        super(message);
    }

    public static CartOwnershipException defaultMessage() {
        return new CartOwnershipException("CartItem is in a cart that doesn't belong to you!");
    }

}
