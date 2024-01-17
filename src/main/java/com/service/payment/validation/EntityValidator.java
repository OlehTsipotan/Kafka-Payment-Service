package com.service.payment.validation;

import com.service.payment.exception.EntityValidationException;
import com.service.payment.exception.FieldViolation;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public abstract class EntityValidator<T> {

    private final Validator validator;

    public void validate(T t) {
        Set<ConstraintViolation<Object>> violations = validator.validate(t);
        if (!violations.isEmpty()) {
            throw new EntityValidationException(String.format("Validation error with %s", t.toString()),
                    violations.stream()
                    .map((constraintViolation) -> new FieldViolation(constraintViolation.getPropertyPath().toString(),
                            constraintViolation.getRootBeanClass().getSimpleName(),
                            constraintViolation.getInvalidValue(), constraintViolation.getMessage())).toList());
        }

    }
}
