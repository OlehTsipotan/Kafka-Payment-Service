package com.service.payment.validation;

import com.service.payment.entity.Customer;
import com.service.payment.exception.InsufficientAvailableBalanceException;
import com.service.payment.exception.InsufficientReserveBalanceException;
import com.service.payment.model.Order;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerBalanceValidator {

    public void validateReservationCreation(@NonNull Customer customer, @NonNull Order order) {
        if (customer.getBalanceAvailable() < order.getTotalPrice()) {
            throw new InsufficientAvailableBalanceException(
                    "Customer with id = " + customer.getId() + " has not enough money to pay for order with id" + " =" +
                            " " + order.getId());
        }
    }

    public void validateReservationRollback(@NonNull Customer customer, @NonNull Order order) {
        if (customer.getBalanceReserved() < order.getTotalPrice()) {
            throw new InsufficientReserveBalanceException(
                    "Customer with id = " + customer.getId() + " has not enough reserved balance to refund for " +
                            "order with id" + " =" + " " + order.getId());
        }
    }

    public void validateReservationConfirmation(@NonNull Customer customer, @NonNull Order order) {
        if (customer.getBalanceReserved() < order.getTotalPrice()) {
            throw new InsufficientReserveBalanceException(
                    "Customer with id = " + customer.getId() + " has not enough reserved balance to refund for " +
                            "order with id" + " =" + " " + order.getId());
        }
    }
}
