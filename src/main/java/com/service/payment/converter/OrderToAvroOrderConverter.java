package com.service.payment.converter;

import com.domain.avro.model.AvroOrder;
import com.service.payment.model.Order;
import lombok.NonNull;
import org.modelmapper.Condition;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OrderToAvroOrderConverter implements Converter<Order, AvroOrder> {

    private final ModelMapper modelMapper;


    public OrderToAvroOrderConverter() {
        this.modelMapper = new ModelMapper();

        org.modelmapper.Converter<UUID, CharSequence> uuidCharSequenceConverter =
                categoryList -> categoryList.getSource().toString();

        Condition notNull = ctx -> ctx.getSource() != null;

        modelMapper.typeMap(Order.class, AvroOrder.class).addMappings(modelMapper -> {
            modelMapper.when(notNull).using(uuidCharSequenceConverter).map(Order::getId, AvroOrder::setId);
        });
    }

    @Override
    public AvroOrder convert(@NonNull Order source) {
        return modelMapper.map(source, AvroOrder.class);
    }
}
