package com.sergejava.telegram_app.exceptions;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(Long orderId) {
        super(String.format("Order with ID '%d' not found!", orderId));
    }

    public OrderNotFoundException(String message) {
        super(message);
    }

}
