package com.sergejava.telegram_app.exceptions;

public class OrderOwnershipException extends RuntimeException {

    public OrderOwnershipException(String message) {
        super(message);
    }

    public OrderOwnershipException() {
        super("The order does not belong to the user.");
    }

}
