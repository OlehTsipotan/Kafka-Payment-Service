package com.service.payment.converter;

import com.service.payment.dto.CustomerDto;
import com.service.payment.entity.Customer;
import lombok.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CustomerDtoToCustomerConverter implements Converter<CustomerDto, Customer> {

    private final ModelMapper modelMapper;


    public CustomerDtoToCustomerConverter() {
        this.modelMapper = new ModelMapper();
    }

    @Override
    @NonNull
    public Customer convert(@NonNull CustomerDto source) {
        return modelMapper.map(source, Customer.class);
    }
}
