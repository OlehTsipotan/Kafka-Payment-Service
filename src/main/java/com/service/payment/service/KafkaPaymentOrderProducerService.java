package com.service.payment.service;

import com.service.payment.model.Order;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaPaymentOrderProducerService {

    private final KafkaTemplate<UUID, Order> template;

    @Value("${kafka.payment-orders.topic}")
    private String topic;

    public void sendOrder(@NonNull Order order) {
        log.info("Sending order: {}", order);
        this.template.send(topic, order.getId(), order);
    }


}
