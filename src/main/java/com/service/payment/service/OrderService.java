package com.service.payment.service;

import com.domain.avro.model.AvroOrder;
import com.service.payment.converter.AvroOrderToOrderConverter;
import com.service.payment.exception.ServiceException;
import com.service.payment.model.Order;
import com.service.payment.model.OrderStatus;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final CustomerService customerService;

    private final KafkaPaymentOrderProducerService kafkaPaymentOrderProducerService;

    private final AvroOrderToOrderConverter converter;

    public void processNewOrder(@NonNull AvroOrder avroOrder) {
        Order order = converter.convert(avroOrder);

        try {
            customerService.makeReservation(order);
            order.setStatus(OrderStatus.ACCEPT);
        } catch (ServiceException e) {
            order.setStatus(OrderStatus.REJECT);
            log.info("Error during reservation creation", e);
        }

        kafkaPaymentOrderProducerService.sendOrder(order);
    }

    public void processRollbackOrder(@NonNull AvroOrder avroOrder) {
        Order order = converter.convert(avroOrder);
        try {
            customerService.rollbackReservation(order);
        } catch (ServiceException e) {
            log.error("Error during rollback reservation", e);
        }

    }

    public void processConfirmationOrder(@NonNull AvroOrder avroOrder) {
        Order order = converter.convert(avroOrder);
        try {
            customerService.confirmReservation(order);
        } catch (ServiceException e) {
            log.error("Error during confirmation reservation", e);
        }
    }

}
