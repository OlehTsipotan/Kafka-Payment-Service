package com.service.payment.validation;

import com.service.payment.entity.Customer;
import com.service.payment.exception.EntityValidationException;
import com.service.payment.exception.FieldViolation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CustomerValidator extends EntityValidator<Customer> {

    public CustomerValidator(Validator validator) {
        super(validator);
    }

    @Override
    public void validate(Customer customer) {
        List<FieldViolation> violations = new ArrayList<>();
        try {
            super.validate(customer);
        } catch (EntityValidationException e) {
            violations = e.getViolations();
        }

        if (!violations.isEmpty()) {
            throw new EntityValidationException("Customer is not valid", violations);
        }

    }
}
