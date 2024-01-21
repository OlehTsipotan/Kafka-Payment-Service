package com.service.payment.converter;

import com.service.payment.dto.CustomerDto;
import com.service.payment.entity.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CustomerFromCustomerDtoUpdaterTest {

    private CustomerFromCustomerDtoUpdater customerFromCustomerDtoUpdater;

    @BeforeEach
    public void setUp() {
        customerFromCustomerDtoUpdater = new CustomerFromCustomerDtoUpdater();
    }

    @Test
    public void update_whenCustomerDtoIsValid_success() {
        Customer customer = new Customer();
        customer.setId(1L);
        CustomerDto customerDto = new CustomerDto();
        customerDto.setId(1L);
        customerFromCustomerDtoUpdater.update(customerDto, customer);

        assertEquals(customerDto.getName(), customer.getName());
    }

    @ParameterizedTest
    @NullSource
    public void update_whenCustomerDtoIsNull_throwIllegalArgumentException(CustomerDto nullCustomerDto) {
        assertThrows(IllegalArgumentException.class,
                () -> customerFromCustomerDtoUpdater.update(nullCustomerDto, new Customer()));
    }

    @ParameterizedTest
    @NullSource
    public void update_whenCustomerIsNull_throwIllegalArgumentException(Customer nullCustomer) {
        assertThrows(IllegalArgumentException.class,
                () -> customerFromCustomerDtoUpdater.update(new CustomerDto(), nullCustomer));
    }
}
