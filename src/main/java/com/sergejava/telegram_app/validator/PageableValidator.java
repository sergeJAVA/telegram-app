package com.sergejava.telegram_app.validator;

import com.sergejava.telegram_app.annotation.ValidPageable;
import com.sergejava.telegram_app.exceptions.InvalidPageOrSizeException;
import com.sergejava.telegram_app.exceptions.InvalidValidationTypeException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PageableValidator implements ConstraintValidator<ValidPageable, Integer> {

    private String type;
    private String errorMessage;

    @Override
    public void initialize(ValidPageable constraintAnnotation) {
        this.type = constraintAnnotation.type();
        this.errorMessage = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }

        switch (type) {
            case "page" -> {
                return validatePage(value);
            }
            case "size" -> {
                return validateSize(value);
            }
            default -> throw new InvalidValidationTypeException(type);
        }
    }

    /**
     * Метод для валидации значения page.
     * @param value значение переменной типа page.
     * @return {@code boolean}
     * @author sergeJAVA
     */
    private boolean validatePage(Integer value) {
        if (value < 0) {
            throw new InvalidPageOrSizeException(errorMessage);
        }
        return true;
    }

    /**
     * Метод для валидации значения size.
     * @param value значение переменной типа size.
     * @return {@code boolean}
     * @author sergeJAVA
     */
    private boolean validateSize(Integer value) {
        if (value < 1) {
            throw new InvalidPageOrSizeException(errorMessage);
        }
        return true;
    }

}
