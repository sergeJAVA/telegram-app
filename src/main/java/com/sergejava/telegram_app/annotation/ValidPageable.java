package com.sergejava.telegram_app.annotation;


import com.sergejava.telegram_app.validator.PageableValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для проверки параметров page и size, которые используются для пагинации запроса.
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PageableValidator.class)
public @interface ValidPageable {

    String message() default "Invalid page or size value! The page parameter must be >= 0, and size must be >= 1.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String type() default "page";

}
