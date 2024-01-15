package com.service.payment.consumer;

import com.service.payment.model.Order;
import com.service.payment.service.OrderProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@EnableKafka
public class KafkaConsumer {

    private final OrderProcessingService orderProcessingService;

    @KafkaListener(id = "orders", topics = "orders", groupId = "payment")
    public void onOrderReceive(Order order) {
        log.info("Received: {}", order);
        orderProcessingService.process(order);
    }

}
