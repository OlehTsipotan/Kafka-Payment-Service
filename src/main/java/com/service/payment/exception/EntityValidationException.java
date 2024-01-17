package com.service.payment.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EntityValidationException extends ValidationException {

    private List<FieldViolation> violations = new ArrayList<>();

    public EntityValidationException(String errorMessage) {
        super(errorMessage);
    }

    public EntityValidationException(String errorMessage, List<FieldViolation> violations) {
        super(errorMessage);
        this.violations = violations;
    }

}