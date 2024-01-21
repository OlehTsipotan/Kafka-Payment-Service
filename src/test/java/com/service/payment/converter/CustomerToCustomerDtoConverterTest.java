package com.service.payment.converter;

import com.service.payment.dto.CustomerDto;
import com.service.payment.entity.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerToCustomerDtoConverterTest {

    private CustomerToCustomerDtoConverter converter;

    @BeforeEach
    public void setUp() {
        converter = new CustomerToCustomerDtoConverter();
    }

    @ParameterizedTest
    @NullSource
    public void convert_whenCustomerIsNull_throwIllegalArgumentException(Customer customer) {
        assertThrows(IllegalArgumentException.class, () -> converter.convert(customer));
    }

    @Test
    public void convert_whenCustomerFieldsAreNull_success() {
        Customer customer = new Customer();

        CustomerDto customerDto = converter.convert(customer);

        assertNotNull(customerDto);
        assertEquals(customerDto.getId(), customer.getId());
        assertEquals(customerDto.getName(), customer.getName());
        assertEquals(customerDto.getBalanceAvailable(), customer.getBalanceAvailable());
    }

    @Test
    public void convert_success() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("Name");
        customer.setBalanceAvailable(1000L);
        customer.setBalanceReserved(1000L);

        CustomerDto customerDto = converter.convert(customer);

        assertNotNull(customerDto);
        assertEquals(customerDto.getId(), customer.getId());
        assertEquals(customerDto.getName(), customer.getName());
        assertEquals(customerDto.getBalanceAvailable(), customer.getBalanceAvailable());
        assertEquals(customerDto.getBalanceReserved(), customer.getBalanceReserved());
    }

}
