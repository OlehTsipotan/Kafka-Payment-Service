package com.service.payment.service;

import com.service.payment.converter.ConverterService;
import com.service.payment.dto.CustomerDto;
import com.service.payment.entity.Customer;
import com.service.payment.exception.*;
import com.service.payment.model.Order;
import com.service.payment.model.Product;
import com.service.payment.repository.CustomerRepository;
import com.service.payment.validation.CustomerBalanceValidator;
import com.service.payment.validation.CustomerValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.BadJpqlGrammarException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class CustomerServiceTest {

    private CustomerService customerService;

    @Mock
    private CustomerValidator customerValidator;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerBalanceValidator customerBalanceValidator;

    @Mock
    private CustomerFromCustomerDtoUpdater customerFromCustomerDtoUpdater;

    @Mock
    private ConverterService converter;

    @BeforeEach
    public void setUp() {
        this.customerService = new CustomerService(customerValidator, customerRepository, customerBalanceValidator,
                customerFromCustomerDtoUpdater, converter);
    }

    @ParameterizedTest
    @NullSource
    public void create_whenCustomerDtoIsNull_throwIllegalArgumentException(CustomerDto nullCustomerDto) {
        assertThrows(IllegalArgumentException.class, () -> customerService.create(nullCustomerDto));
    }

    @Test
    public void create_whenCustomerRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        doThrow(BadJpqlGrammarException.class).when(customerRepository).existsById(any());

        CustomerDto customerDto = new CustomerDto();
        customerDto.setId(1L);
        Customer customer = new Customer();
        customer.setId(1L);

        when(converter.convert(customerDto, Customer.class)).thenReturn(customer);

        assertThrows(ServiceException.class, () -> customerService.create(customerDto));

        verify(customerValidator).validate(any());
        verify(customerRepository).existsById(any());
        verifyNoMoreInteractions(customerRepository);
    }

    @Test
    public void create_whenValidatorThrowsEntityValidationException_throwEntityValidationException() {
        doThrow(EntityValidationException.class).when(customerValidator).validate(any());

        assertThrows(EntityValidationException.class, () -> customerService.create(new CustomerDto()));

        verify(customerValidator).validate(any());
    }

    @Test
    public void create_whenCustomerAlreadyExists_throwEntityAlreadyExistsException() {
        when(customerRepository.existsById(any())).thenReturn(true);

        CustomerDto customerDto = new CustomerDto();
        customerDto.setId(1L);
        Customer customer = new Customer();
        customer.setId(1L);

        when(converter.convert(customerDto, Customer.class)).thenReturn(customer);

        assertThrows(EntityAlreadyExistsException.class, () -> customerService.create(customerDto));

        verify(customerValidator).validate(any());
    }

    @Test
    public void create_success() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setId(1L);
        Customer customer = new Customer();
        customer.setId(1L);

        when(converter.convert(customerDto, Customer.class)).thenReturn(customer);
        when(customerRepository.save(any())).thenReturn(customer);
        when(customerRepository.existsById(any())).thenReturn(false);

        assertEquals(customerDto.getId(), customerService.create(customerDto));

        verify(customerRepository).save(customer);
        verify(customerRepository).existsById(any());
        verifyNoMoreInteractions(customerRepository);

        verify(customerValidator).validate(customer);
        verifyNoMoreInteractions(customerBalanceValidator);

        verify(converter).convert(customerDto, Customer.class);
        verifyNoMoreInteractions(converter);
    }

    @ParameterizedTest
    @NullSource
    public void update_whenCustomerDtoIsNull_throwIllegalArgumentException(CustomerDto CustomerDto) {
        assertThrows(IllegalArgumentException.class, () -> customerService.update(CustomerDto, 1L));

        verifyNoInteractions(customerRepository);
        verifyNoInteractions(customerBalanceValidator);
    }

    @Test
    public void update_whenRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        doThrow(BadJpqlGrammarException.class).when(customerRepository).findById(any());

        CustomerDto customerDto = new CustomerDto();
        customerDto.setId(1L);

        assertThrows(ServiceException.class, () -> customerService.update(customerDto, 1L));

        verify(customerRepository).findById(any());
        verifyNoMoreInteractions(customerRepository);
    }

    @Test
    public void update_whenValidatorThrowsEntityValidationException_throwValidationException() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setId(1L);
        Customer customer = new Customer();
        customer.setId(1L);

        when(customerRepository.findById(any())).thenReturn(Optional.of(customer));

        doThrow(EntityValidationException.class).when(customerValidator).validate(any());

        assertThrows(EntityValidationException.class, () -> customerService.update(customerDto, 1L));

        verify(customerValidator).validate(any());

        verify(customerRepository).findById(any());
        verifyNoMoreInteractions(customerRepository);

        verify(customerFromCustomerDtoUpdater).update(any(), any());
        verifyNoMoreInteractions(customerFromCustomerDtoUpdater);

        verifyNoInteractions(converter);
    }

    @Test
    public void update_whenCustomerDoesNotExists_throwEntityNotFoundException() {
        when(customerRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> customerService.update(new CustomerDto(), 1L));

        verify(customerRepository).findById(any());
        verifyNoMoreInteractions(customerRepository);

        verifyNoInteractions(customerFromCustomerDtoUpdater);
        verifyNoInteractions(converter);
        verifyNoInteractions(customerBalanceValidator);
    }

    @Test
    public void update_success() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setId(1L);
        Customer customer = new Customer();
        customer.setId(1L);

        when(customerRepository.findById(any())).thenReturn(Optional.ofNullable(customer));
        when(customerRepository.save(any())).thenReturn(customer);
        when(converter.convert(customer, CustomerDto.class)).thenReturn(customerDto);

        assertEquals(customerDto, customerService.update(customerDto, 1L));

        verify(customerRepository).findById(any());
        verify(customerRepository).save(customer);
        verifyNoMoreInteractions(customerRepository);

        verify(customerValidator).validate(customer);
        verifyNoMoreInteractions(customerBalanceValidator);

        verify(customerFromCustomerDtoUpdater).update(customerDto, customer);
        verifyNoMoreInteractions(customerFromCustomerDtoUpdater);

        verify(converter).convert(customer, CustomerDto.class);
        verifyNoMoreInteractions(converter);
    }

    @Test
    public void deleteById_whenCustomerRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        doThrow(BadJpqlGrammarException.class).when(customerRepository).deleteById(any());

        assertThrows(ServiceException.class, () -> customerService.deleteById(1L));

        verify(customerRepository).existsById(any());
    }

    @Test
    public void deleteById_whenCustomerDoesNotExists_throwEntityDoesNotExistsException() {
        when(customerRepository.existsById(any())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> customerService.deleteById(1L));

        verify(customerRepository).existsById(any());
    }

    @ParameterizedTest
    @NullSource
    public void deleteById_whenIdIsNull_throwIllegalArgumentException(Long nullId) {
        assertThrows(IllegalArgumentException.class, () -> customerService.deleteById(nullId));

        verifyNoInteractions(customerRepository);
    }

    @Test
    public void deleteById_success() {
        when(customerRepository.existsById(any())).thenReturn(true);

        customerService.deleteById(1L);

        verify(customerRepository).deleteById(any());
    }

    @ParameterizedTest
    @NullSource
    public void findById_whenIdIsNull_throwIllegalArgumentException(Long nullId) {
        assertThrows(IllegalArgumentException.class, () -> customerService.findById(nullId));
    }

    @Test
    public void findById_whenCustomerRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        doThrow(BadJpqlGrammarException.class).when(customerRepository).findById(any());

        assertThrows(ServiceException.class, () -> customerService.findById(1L));

        verify(customerRepository).findById(any());
    }

    @Test
    public void findById_success() {
        Customer customer = new Customer();
        customer.setId(1L);

        when(customerRepository.findById(any())).thenReturn(Optional.ofNullable(customer));

        assertEquals(customer, customerService.findById(1L));

        verify(customerRepository).findById(any());
        verifyNoMoreInteractions(customerRepository);

        verifyNoMoreInteractions(converter);
    }

    @Test
    public void findAll_whenRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        when(customerRepository.findAll(any(Pageable.class))).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> customerService.findAll(Pageable.unpaged()));

        verify(customerRepository).findAll(any(Pageable.class));
    }

    @ParameterizedTest
    @NullSource
    public void findAll_whenPageableIsNull_throwIllegalArgumentException(Pageable nullPageable) {
        assertThrows(IllegalArgumentException.class, () -> customerService.findAll(nullPageable));

        verifyNoInteractions(customerRepository);
    }

    @Test
    public void findAll_success() {
        when(customerRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        Pageable pageable = mock(Pageable.class);
        when(pageable.getSort()).thenReturn(mock(org.springframework.data.domain.Sort.class));

        assertDoesNotThrow(() -> customerService.findAll(pageable));

        verify(customerRepository).findAll(any(Pageable.class));
    }

    @ParameterizedTest
    @NullSource
    public void findByIdAsDto_whenIdIsNull_throwIllegalArgumentException(Long nullId) {
        assertThrows(IllegalArgumentException.class, () -> customerService.findByIdAsDto(nullId));
    }

    @Test
    public void findByIdAsDto_whenCustomerRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        doThrow(BadJpqlGrammarException.class).when(customerRepository).findById(any());

        assertThrows(ServiceException.class, () -> customerService.findByIdAsDto(1L));

        verify(customerRepository).findById(any());
    }

    @Test
    public void findByIdAsDto_success() {
        Customer customer = new Customer();
        customer.setId(1L);
        CustomerDto customerDto = new CustomerDto();
        customerDto.setId(1L);

        when(customerRepository.findById(any())).thenReturn(Optional.ofNullable(customer));
        when(converter.convert(customer, CustomerDto.class)).thenReturn(customerDto);

        assertEquals(customerDto, customerService.findByIdAsDto(1L));

        verify(converter).convert(customer, CustomerDto.class);

        verify(customerRepository).findById(any());
        verifyNoMoreInteractions(customerRepository);

        verifyNoMoreInteractions(converter);
    }

    @ParameterizedTest
    @NullSource
    public void createReservation_whenOrderIsNull_throwIllegalArgumentException(Order nullOrder) {
        assertThrows(IllegalArgumentException.class, () -> customerService.createReservation(nullOrder));
    }

    @Test
    public void createReservation_whenCustomerReservationValidatorThrowsValidationException_throwValidationException() {
        Order order = new Order();
        order.setCustomerId(1L);

        Customer customer = new Customer();
        customer.setId(1L);

        when(customerRepository.findById(any())).thenReturn(Optional.of(customer));

        doThrow(ValidationException.class).when(customerBalanceValidator).validateReservationCreation(customer, order);

        assertThrows(ValidationException.class, () -> customerService.createReservation(order));

        verify(customerBalanceValidator).validateReservationCreation(customer, order);
    }

    @Test
    public void createReservation_whenCustomerDoesNotExists_throwEntityNotFoundException() {
        Order order = new Order();
        order.setCustomerId(1L);

        when(customerRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> customerService.createReservation(order));

        verify(customerRepository).findById(any());
    }

    @Test
    public void createReservation_whenCustomerRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        Order order = new Order();
        order.setCustomerId(1L);

        when(customerRepository.save(any())).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> customerService.createReservation(order));

        verify(customerRepository).findById(any());
    }

    @Test
    public void createReservation_success() {
        Order order = new Order();
        order.setCustomerId(1L);

        Product product = new Product();
        product.setId(1L);
        product.setQuantity(1);
        product.setPrice(100L);

        order.setProduct(product);

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setBalanceAvailable(1000L);
        customer.setBalanceReserved(0L);

        when(customerRepository.findById(any())).thenReturn(Optional.of(customer));

        customerService.createReservation(order);

        verify(customerRepository).findById(any());
        verify(customerRepository).save(customer);
        verifyNoMoreInteractions(customerRepository);

        verify(customerBalanceValidator).validateReservationCreation(customer, order);
        verifyNoMoreInteractions(customerBalanceValidator);
    }

    @ParameterizedTest
    @NullSource
    public void rollbackReservation_whenOrderIsNull_throwIllegalArgumentException(Order nullOrder) {
        assertThrows(IllegalArgumentException.class, () -> customerService.rollbackReservation(nullOrder));
    }

    @Test
    public void rollbackReservation_whenCustomerReservationValidatorThrowsValidationException_throwValidationException() {
        Order order = new Order();
        order.setCustomerId(1L);

        Customer customer = new Customer();
        customer.setId(1L);

        when(customerRepository.findById(any())).thenReturn(Optional.of(customer));

        doThrow(ValidationException.class).when(customerBalanceValidator).validateReservationRollback(customer, order);

        assertThrows(ValidationException.class, () -> customerService.rollbackReservation(order));

        verify(customerBalanceValidator).validateReservationRollback(customer, order);
    }

    @Test
    public void rollbackReservation_whenCustomerDoesNotExists_throwEntityNotFoundException() {
        Order order = new Order();
        order.setCustomerId(1L);

        when(customerRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> customerService.rollbackReservation(order));

        verify(customerRepository).findById(any());
    }

    @Test
    public void rollbackReservation_whenCustomerRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        Order order = new Order();
        order.setCustomerId(1L);

        when(customerRepository.save(any())).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> customerService.rollbackReservation(order));

        verify(customerRepository).findById(any());
    }

    @Test
    public void rollbackReservation_success() {
        Order order = new Order();
        order.setCustomerId(1L);

        Product product = new Product();
        product.setId(1L);
        product.setQuantity(1);
        product.setPrice(100L);

        order.setProduct(product);

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setBalanceAvailable(1000L);
        customer.setBalanceReserved(0L);

        when(customerRepository.findById(any())).thenReturn(Optional.of(customer));

        customerService.rollbackReservation(order);

        verify(customerRepository).findById(any());
        verify(customerRepository).save(customer);
        verifyNoMoreInteractions(customerRepository);

        verify(customerBalanceValidator).validateReservationRollback(customer, order);
        verifyNoMoreInteractions(customerBalanceValidator);
    }

    @ParameterizedTest
    @NullSource
    public void confirmReservation_whenOrderIsNull_throwIllegalArgumentException(Order nullOrder) {
        assertThrows(IllegalArgumentException.class, () -> customerService.confirmReservation(nullOrder));
    }

    @Test
    public void confirmReservation_whenCustomerReservationValidatorThrowsValidationException_throwValidationException() {
        Order order = new Order();
        order.setCustomerId(1L);

        Customer customer = new Customer();
        customer.setId(1L);

        when(customerRepository.findById(any())).thenReturn(Optional.of(customer));

        doThrow(ValidationException.class).when(customerBalanceValidator).validateReservationConfirmation(customer, order);

        assertThrows(ValidationException.class, () -> customerService.confirmReservation(order));

        verify(customerBalanceValidator).validateReservationConfirmation(customer, order);
    }

    @Test
    public void confirmReservation_whenCustomerDoesNotExists_throwEntityNotFoundException() {
        Order order = new Order();
        order.setCustomerId(1L);

        when(customerRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> customerService.confirmReservation(order));

        verify(customerRepository).findById(any());
    }

    @Test
    public void confirmReservation_whenCustomerRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        Order order = new Order();
        order.setCustomerId(1L);

        when(customerRepository.save(any())).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> customerService.confirmReservation(order));

        verify(customerRepository).findById(any());
    }

    @Test
    public void confirmReservation_success() {
        Order order = new Order();
        order.setCustomerId(1L);

        Product product = new Product();
        product.setId(1L);
        product.setQuantity(1);
        product.setPrice(100L);

        order.setProduct(product);

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setBalanceAvailable(1000L);
        customer.setBalanceReserved(0L);

        when(customerRepository.findById(any())).thenReturn(Optional.of(customer));

        customerService.confirmReservation(order);

        verify(customerRepository).findById(any());
        verify(customerRepository).save(customer);
        verifyNoMoreInteractions(customerRepository);

        verify(customerBalanceValidator).validateReservationConfirmation(customer, order);
        verifyNoMoreInteractions(customerBalanceValidator);
    }
}

