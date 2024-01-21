package com.service.payment.converter;

import com.service.payment.dto.CustomerDto;
import com.service.payment.entity.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerDtoToCustomerConverterTest {

    private CustomerDtoToCustomerConverter converter;

    @BeforeEach
    public void setUp() {
        converter = new CustomerDtoToCustomerConverter();
    }

    @ParameterizedTest
    @NullSource
    public void convert_whenCustomerDtoIsNull_throwIllegalArgumentException(CustomerDto customerDto) {
        assertThrows(IllegalArgumentException.class, () -> converter.convert(customerDto));
    }

    @Test
    public void convert_whenCustomerDtoFieldsAreNull_success() {
        CustomerDto customerDto = new CustomerDto();

        Customer customer = converter.convert(customerDto);

        assertNotNull(customer);
        assertEquals(customer.getId(), customerDto.getId());
        assertEquals(customer.getName(), customerDto.getName());
        assertEquals(customer.getBalanceAvailable(), customerDto.getBalanceAvailable());
    }

    @Test
    public void convert_success() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setId(1L);
        customerDto.setName("Name");
        customerDto.setBalanceAvailable(1000L);
        customerDto.setBalanceReserved(1000L);

        Customer customer = converter.convert(customerDto);

        assertNotNull(customer);
        assertEquals(customer.getId(), customerDto.getId());
        assertEquals(customer.getName(), customerDto.getName());
        assertEquals(customer.getBalanceAvailable(), customerDto.getBalanceAvailable());
        assertEquals(customer.getBalanceReserved(), customerDto.getBalanceReserved());
    }

}
