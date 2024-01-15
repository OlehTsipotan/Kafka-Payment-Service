package com.service.payment.service;

import com.service.payment.entity.Customer;
import com.service.payment.exception.EntityNotFoundException;
import com.service.payment.exception.InsufficientAvailableBalanceException;
import com.service.payment.exception.InsufficientReserveBalanceException;
import com.service.payment.exception.ServiceException;
import com.service.payment.model.Order;
import com.service.payment.repository.CustomerRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional
    public Long save(@NonNull Customer customer) {
        Customer savedCustomer = execute(() -> customerRepository.save(customer));
        log.info("Saved Customer {}", savedCustomer);
        return savedCustomer.getId();
    }

    public Customer findById(@NonNull Long id) {
        Customer customer = execute(() -> customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no Customer with id = " + id)));
        log.debug("Retrieved Customer by id = {}", id);
        return customer;
    }

    @Transactional
    public void makeReservation(@NonNull Order order) {
        Customer customer = findById(order.getCustomerId());
        Long orderPrice = order.getTotalPrice();
        if (customer.getBalanceAvailable() < orderPrice) {
            throw new InsufficientAvailableBalanceException(
                    "Customer with id = " + customer.getId() + " has not enough money to pay for order with id" + " =" +
                            " " + order.getId());
        }
        customer.setBalanceReserved(customer.getBalanceReserved() + orderPrice);
        customer.setBalanceAvailable(customer.getBalanceAvailable() - orderPrice);

        execute(() -> customerRepository.save(customer));
    }

    @Transactional
    public void cancelReservation(@NonNull Order order) {
        Customer customer = findById(order.getCustomerId());
        Long orderPrice = order.getTotalPrice();
        if (customer.getBalanceReserved() < orderPrice) {
            throw new InsufficientReserveBalanceException(
                    "Customer with id = " + customer.getId() + " has not enough reserved balance to refund for " +
                            "order with id" + " =" + " " + order.getId());
        }
        customer.setBalanceReserved(customer.getBalanceReserved() - orderPrice);
        customer.setBalanceAvailable(customer.getBalanceAvailable() + orderPrice);

        execute(() -> customerRepository.save(customer));
    }

    @Transactional
    public void confirmReservation(@NonNull Order order) {
        Customer customer = findById(order.getCustomerId());
        Long orderPrice = order.getTotalPrice();
        if (customer.getBalanceReserved() < orderPrice) {
            throw new InsufficientReserveBalanceException(
                    "Customer with id = " + customer.getId() + " has not enough reserved balance to refund for " +
                            "order with id" + " =" + " " + order.getId());
        }
        customer.setBalanceReserved(customer.getBalanceReserved() - orderPrice);

        execute(() -> customerRepository.save(customer));
    }

    private <T> T execute(DaoSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (DataAccessException e) {
            throw new ServiceException("DAO operation failed", e);
        }
    }

    private void execute(DaoProcessor processor) {
        try {
            processor.process();
        } catch (DataAccessException e) {
            throw new ServiceException("DAO operation failed", e);
        }
    }

    @FunctionalInterface
    public interface DaoSupplier<T> {
        T get();
    }

    @FunctionalInterface
    public interface DaoProcessor {
        void process();
    }
}
