package com.service.payment.converter;

import com.service.payment.dto.CustomerDto;
import com.service.payment.entity.Customer;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class CustomerFromCustomerDtoUpdater {

    private final ModelMapper modelMapper;

    public CustomerFromCustomerDtoUpdater() {
        this.modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);

    }

    public void update(CustomerDto customerDto, Customer customer) {
        modelMapper.map(customerDto, customer);
    }
}
