package com.service.payment.service;

import com.service.payment.entity.Customer;
import com.service.payment.exception.EntityNotFoundException;
import com.service.payment.exception.ServiceException;
import com.service.payment.model.Order;
import com.service.payment.repository.CustomerRepository;
import com.service.payment.validation.CustomerBalanceValidator;
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

    private final CustomerBalanceValidator customerReservationValidator;

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

        customerReservationValidator.validateReservationCreation(customer, order);

        customer.setBalanceReserved(customer.getBalanceReserved() + order.getTotalPrice());
        customer.setBalanceAvailable(customer.getBalanceAvailable() - order.getTotalPrice());

        execute(() -> customerRepository.save(customer));
        log.info("Customer reservation created: {} for Order: {}", customer, order);
    }

    @Transactional
    public void rollbackReservation(@NonNull Order order) {
        Customer customer = findById(order.getCustomerId());

        customerReservationValidator.validateReservationRollback(customer, order);

        customer.setBalanceReserved(customer.getBalanceReserved() - order.getTotalPrice());
        customer.setBalanceAvailable(customer.getBalanceAvailable() + order.getTotalPrice());

        execute(() -> customerRepository.save(customer));
        log.info("Customer reservation rollbacked: {} for Order: {}", customer, order);
    }

    @Transactional
    public void confirmReservation(@NonNull Order order) {
        Customer customer = findById(order.getCustomerId());

        customerReservationValidator.validateReservationConfirmation(customer, order);

        customer.setBalanceReserved(customer.getBalanceReserved() - order.getTotalPrice());

        execute(() -> customerRepository.save(customer));
        log.info("Customer reservation confirmed: {} for Order: {}", customer, order);
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
