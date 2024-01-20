package com.service.payment.service;

import com.service.payment.converter.ConverterService;
import com.service.payment.converter.CustomerFromCustomerDtoUpdater;
import com.service.payment.dto.CustomerDto;
import com.service.payment.dto.DtoSearchResponse;
import com.service.payment.entity.Customer;
import com.service.payment.exception.EntityAlreadyExistsException;
import com.service.payment.exception.EntityNotFoundException;
import com.service.payment.exception.ServiceException;
import com.service.payment.model.Order;
import com.service.payment.repository.CustomerRepository;
import com.service.payment.validation.CustomerBalanceValidator;
import com.service.payment.validation.CustomerValidator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerValidator customerValidator;

    private final CustomerRepository customerRepository;

    private final CustomerBalanceValidator customerReservationValidator;

    private final CustomerFromCustomerDtoUpdater customerFromCustomerDtoUpdater;

    private final ConverterService converter;

    @Transactional
    public Long create(@NonNull CustomerDto customerDto) {
        Customer customer = convertToEntity(customerDto);
        customerValidator.validate(customer);
        Customer savedCustomer = execute(() -> {
            if (customer.getId() != null && customerRepository.existsById(customer.getId())) {
                throw new EntityAlreadyExistsException("Customer with id = " + customer.getId() + " already exists");
            }
            return customerRepository.save(customer);
        });
        log.info("Created Customer {}", savedCustomer);
        return savedCustomer.getId();
    }

    @Transactional
    public CustomerDto update(@NonNull CustomerDto customerDto, @NonNull Long id) {
        Customer customerToUpdate = execute(() -> {
            Customer customer = customerRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("There is no Customer to update with id = " + id));

            customerFromCustomerDtoUpdater.update(customerDto, customer);
            customerValidator.validate(customer);

            return customerRepository.save(customer);
        });
        log.info("Updated Customer {}", customerToUpdate);
        return convertToDto(customerToUpdate);
    }

    public Customer findById(@NonNull Long id) {
        Customer customer = execute(() -> customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no Customer with id = " + id)));
        log.debug("Retrieved Customer by id = {}", id);
        return customer;
    }

    public CustomerDto findByIdAsDto(@NonNull Long id) {
        Customer customer = execute(() -> customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no Customer with id = " + id)));
        log.debug("Retrieved Customer by id = {}", id);
        return convertToDto(customer);
    }

    @Transactional
    public void deleteById(@NonNull Long id) {
        execute(() -> {
            if (!customerRepository.existsById(id)) {
                throw new EntityNotFoundException("There is no Custoemr to delete with id = " + id);
            }
            customerRepository.deleteById(id);
        });
        log.info("Deleted Customer id = {}", id);
    }

    public DtoSearchResponse findAll(@NonNull Pageable pageable) {
        List<CustomerDto> carDTOList =
                execute(() -> customerRepository.findAll(pageable)).stream().map(this::convertToDto).toList();
        log.debug("Retrieved All {} Cars", carDTOList.size());
        return DtoSearchResponse.builder().offset(pageable.getOffset()).limit(pageable.getPageSize())
                .total(carDTOList.size()).sort(pageable.getSort().toString()).data(carDTOList).build();

    }

    @Transactional
    public void createReservation(@NonNull Order order) {
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

    private CustomerDto convertToDto(Customer customer) {
        return converter.convert(customer, CustomerDto.class);
    }

    private Customer convertToEntity(CustomerDto customerDto) {
        return converter.convert(customerDto, Customer.class);
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
