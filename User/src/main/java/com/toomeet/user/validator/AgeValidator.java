package com.toomeet.user.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AgeValidatorClass.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AgeValidator {
    String message() default "User must be at least {value} years old";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int value();
}
