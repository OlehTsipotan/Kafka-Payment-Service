package com.service.payment.service;

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

    public void processNewOrder(@NonNull Order order) {
        try {
            customerService.makeReservation(order);
            order.setStatus(OrderStatus.ACCEPT);
        } catch (ServiceException e) {
            log.error("ServiceException: {}", e.getMessage());
            order.setDescription(e.getMessage());
            order.setStatus(OrderStatus.REJECT);
        }
    }

    public void processRollbackOrder(@NonNull Order order) {
        customerService.cancelReservation(order);
    }

    public void processConfirmationOrder(@NonNull Order order) {
        customerService.confirmReservation(order);
    }

}
