package com.georent.validation;

import com.georent.dto.TimeRangeable;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StartBeforeEndDateValidator implements ConstraintValidator<StartBeforeEndDate, TimeRangeable> {

    @Override
    public void initialize(StartBeforeEndDate constraintAnnotation) {
    }

    @Override
    public boolean isValid(TimeRangeable value, ConstraintValidatorContext context) {
        return value.getStartTime().isBefore(value.getEndTime());
    }
}
