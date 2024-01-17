package com.service.payment.converter;

import com.service.payment.dto.CustomerDto;
import com.service.payment.entity.Customer;
import lombok.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CustomerToCustomerDtoConverter implements Converter<Customer, CustomerDto> {

    private final ModelMapper modelMapper;


    public CustomerToCustomerDtoConverter() {
        this.modelMapper = new ModelMapper();
    }

    @Override
    @NonNull
    public CustomerDto convert(@NonNull Customer source) {
        return modelMapper.map(source, CustomerDto.class);
    }
}
