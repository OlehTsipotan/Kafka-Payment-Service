package com.service.payment.converter;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConverterService extends GenericConversionService {

    private final List<Converter<?, ?>> converters;

    @PostConstruct
    public void init() {
        converters.forEach(this::addConverter);
    }
}