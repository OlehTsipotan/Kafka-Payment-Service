package com.service.payment.converter;

import com.domain.avro.model.AvroOrder;
import com.service.payment.model.Order;
import lombok.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AvroOrderToOrderConverter implements Converter<AvroOrder, Order> {

    private final ModelMapper modelMapper;

    public AvroOrderToOrderConverter() {
        this.modelMapper = new ModelMapper();
    }

    @Override
    @NonNull
    public Order convert(@NonNull AvroOrder source) {
        return modelMapper.map(source, Order.class);
    }
}
