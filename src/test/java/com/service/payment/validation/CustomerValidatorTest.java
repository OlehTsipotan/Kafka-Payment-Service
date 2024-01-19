package com.service.payment.validation;

import com.service.payment.entity.Customer;
import com.service.payment.exception.EntityValidationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
public class CustomerValidatorTest {

    private final Validator jakartaValidator = Validation.buildDefaultValidatorFactory().getValidator();
    private CustomerValidator validator;

    @BeforeEach
    public void setUp() {
        validator = new CustomerValidator(jakartaValidator);
    }

    @Test
    public void validate_whenCustomerIsValid() {
        Customer customer = new Customer();

        customer.setId(1L);
        customer.setName("testName");
        customer.setBalanceAvailable(100L);
        customer.setBalanceReserved(0L);

        assertDoesNotThrow(() -> validator.validate(customer));
    }

    @Test
    public void validate_whenCustomerIsInvalid() {
        Customer customer = new Customer();

        customer.setId(1L);
        customer.setName("testName");
        customer.setBalanceAvailable(100L);
        customer.setBalanceReserved(-50L);

        assertThrows(EntityValidationException.class, () -> validator.validate(customer));
    }

    @ParameterizedTest
    @NullSource
    public void validate_whenCustomerIsNull_throwIllegalArgumentException(Customer customer) {
        assertThrows(IllegalArgumentException.class, () -> validator.validate(customer));
    }
}
