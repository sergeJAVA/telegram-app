package com.sergejava.telegram_app.handler;

import com.sergejava.telegram_app.exceptions.CategoryAlreadyExistsException;
import com.sergejava.telegram_app.exceptions.SizeNotFoundByNameException;
import com.sergejava.telegram_app.exceptions.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ex.getMessage());
    }

    @ExceptionHandler(CategoryAlreadyExistsException.class)
    public ResponseEntity<?> handleCategoryAlreadyExists(CategoryAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ex.getMessage());
    }

    @ExceptionHandler(SizeNotFoundByNameException.class)
    public ResponseEntity<?> handleSizeNotFoundByName(SizeNotFoundByNameException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

}
