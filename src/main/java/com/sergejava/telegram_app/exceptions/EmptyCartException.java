package com.sergejava.telegram_app.exceptions;

public class EmptyCartException extends RuntimeException {

    public EmptyCartException(String message) {
        super(message);
    }

    public EmptyCartException() {
        super("Your cart is empty, you cannot place an order.");
    }

}
