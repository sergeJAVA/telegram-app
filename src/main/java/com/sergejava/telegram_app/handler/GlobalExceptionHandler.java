package com.sergejava.telegram_app.handler;

import com.sergejava.telegram_app.exceptions.AdminNotSpecifiedException;
import com.sergejava.telegram_app.exceptions.CartItemNotFoundException;
import com.sergejava.telegram_app.exceptions.CartItemRemovedException;
import com.sergejava.telegram_app.exceptions.CartNotFoundException;
import com.sergejava.telegram_app.exceptions.CartOwnershipException;
import com.sergejava.telegram_app.exceptions.CategoryAlreadyExistsException;
import com.sergejava.telegram_app.exceptions.CategoryNotFoundException;
import com.sergejava.telegram_app.exceptions.EmptyCartException;
import com.sergejava.telegram_app.exceptions.ImageUrlsNullOrEmptyException;
import com.sergejava.telegram_app.exceptions.InsufficientStockException;
import com.sergejava.telegram_app.exceptions.InvalidImageUrlException;
import com.sergejava.telegram_app.exceptions.InvalidOrderStatusException;
import com.sergejava.telegram_app.exceptions.InvalidPageOrSizeException;
import com.sergejava.telegram_app.exceptions.InvalidSizeNameException;
import com.sergejava.telegram_app.exceptions.InvalidValidationTypeException;
import com.sergejava.telegram_app.exceptions.OrderAlreadyCancelledException;
import com.sergejava.telegram_app.exceptions.OrderNotFoundException;
import com.sergejava.telegram_app.exceptions.OrderOwnershipException;
import com.sergejava.telegram_app.exceptions.ProductNotFoundException;
import com.sergejava.telegram_app.exceptions.RoleNotFoundException;
import com.sergejava.telegram_app.exceptions.SizeNotFoundByNameException;
import com.sergejava.telegram_app.exceptions.UserAlreadyExistsException;
import com.sergejava.telegram_app.exceptions.UserIdNotEqualsToAdminIdException;
import com.sergejava.telegram_app.exceptions.UserNotFoundException;
import jakarta.validation.UnexpectedTypeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * <b>Обработчик ошибок.</b>
 */
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

    @ExceptionHandler(ImageUrlsNullOrEmptyException.class)
    public ResponseEntity<?> handleImageUrlsNullOrEmpty(ImageUrlsNullOrEmptyException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidImageUrlException.class)
    public ResponseEntity<?> handleImageUrlsNullOrEmpty(InvalidImageUrlException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> validationArgument(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidPageOrSizeException.class)
    public ResponseEntity<?> handleInvalidPageOrSize(InvalidPageOrSizeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidValidationTypeException.class)
    public ResponseEntity<?> handleInvalidValidationType(InvalidValidationTypeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<?> handleCategoryNotFound(CategoryNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<?> handleInsufficientStock(InsufficientStockException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidSizeNameException.class)
    public ResponseEntity<?> handleInvalidSizeName(InvalidSizeNameException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<?> handleProductNotFound(ProductNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<?> handleCartNotFound(CartNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(EmptyCartException.class)
    public ResponseEntity<?> handleEmptyCart(EmptyCartException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<?> handleOrderNotFound(OrderNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(OrderAlreadyCancelledException.class)
    public ResponseEntity<?> handleOrderAlreadyCancelled(OrderAlreadyCancelledException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidOrderStatusException.class)
    public ResponseEntity<?> handleInvalidOrderStatus(InvalidOrderStatusException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        return ResponseEntity
                .badRequest()
                .body(ex.getMessage());
    }

    @ExceptionHandler(UnexpectedTypeException.class)
    public ResponseEntity<?> handleUnexpectedType(UnexpectedTypeException ex) {
        return ResponseEntity
                .badRequest()
                .body(ex.getMessage());
    }

    @ExceptionHandler(CartItemNotFoundException.class)
    public ResponseEntity<?> handleCartItemNotFound(CartItemNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(CartItemRemovedException.class)
    public ResponseEntity<?> handleCartItemRemoved(CartItemRemovedException ex) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ex.getMessage());
    }

    @ExceptionHandler(AdminNotSpecifiedException.class)
    public ResponseEntity<?> handleAdminNotSpecified(AdminNotSpecifiedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(UserIdNotEqualsToAdminIdException.class)
    public ResponseEntity<?> handleUserIdNotEqualsToAdminId(UserIdNotEqualsToAdminIdException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<?> handleRoleNotFound(RoleNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(OrderOwnershipException.class)
    public ResponseEntity<?> handleOrderOwnership(OrderOwnershipException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(CartOwnershipException.class)
    public ResponseEntity<?> handleCartOwnership(CartOwnershipException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

}
