package com.service.payment.service;

import com.service.payment.model.Order;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderProcessingService {

    private final OrderService orderService;

    private final KafkaPaymentOrderProducerService kafkaPaymentOrderProducerService;

    public void process(@NonNull Order order) {
        order.setSource("PAYMENT");
        switch (order.getStatus()) {
            case NEW:
                orderService.processNewOrder(order);
                kafkaPaymentOrderProducerService.sendOrder(order);
                break;
            case ROLLBACK:
                orderService.processRollbackOrder(order);
                break;
            case CONFIRMATION:
                orderService.processConfirmationOrder(order);
                break;
            default:
                log.warn("Unknown order status: {}", order.getStatus());
        }
    }
}
