package com.service.payment.service;

import com.domain.avro.model.AvroOrder;
import com.service.payment.converter.AvroOrderToOrderConverter;
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

    private final AvroOrderToOrderConverter converter;

    public void process(@NonNull AvroOrder avroOrder) {
        switch (avroOrder.getStatus()) {
            case NEW:
                orderService.processNewOrder(avroOrder);
                break;
            case ROLLBACK:
                orderService.processRollbackOrder(avroOrder);
                break;
            case CONFIRMATION:
                orderService.processConfirmationOrder(avroOrder);
                break;
            default:
                log.warn("Unknown order status: {}", avroOrder.getStatus());
        }
    }
}
