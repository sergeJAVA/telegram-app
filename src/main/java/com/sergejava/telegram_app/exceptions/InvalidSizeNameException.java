package com.sergejava.telegram_app.exceptions;

public class InvalidSizeNameException extends RuntimeException {

    public InvalidSizeNameException(String sizeName) {
        super("The size of the product named '" + sizeName + "' was not found! \n" +
                "Perhaps the item is not in stock in that size, or you entered the wrong size name!");
    }

}
