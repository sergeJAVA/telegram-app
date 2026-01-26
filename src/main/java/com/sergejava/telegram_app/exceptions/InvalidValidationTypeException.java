package com.sergejava.telegram_app.exceptions;

public class InvalidValidationTypeException extends RuntimeException {

    public InvalidValidationTypeException(String value) {
        super(String.format("The value from the @ValidPageable annotation argument type <%s> is not processed," +
                " please use page or size as the value.", value));
    }

}
