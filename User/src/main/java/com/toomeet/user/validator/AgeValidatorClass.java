package com.toomeet.user.validator;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;
import java.util.Date;

public class AgeValidatorClass implements ConstraintValidator<AgeValidator, Date> {

    private int minAge;

    @Override
    public void initialize(AgeValidator ageValidator) {
        this.minAge = ageValidator.value();
    }

    @Override
    public boolean isValid(Date dateOfBirth, ConstraintValidatorContext constraintValidatorContext) {
        if (dateOfBirth == null) return false;
        LocalDate birthDate = LocalDate.ofInstant(dateOfBirth.toInstant(), java.time.ZoneId.systemDefault());
        LocalDate currentDate = LocalDate.now();
        int age = Period.between(birthDate, currentDate).getYears();
        return age >= minAge;
    }
}
