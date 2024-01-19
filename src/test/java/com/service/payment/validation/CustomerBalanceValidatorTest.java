package com.service.payment.validation;

import com.service.payment.entity.Customer;
import com.service.payment.exception.InsufficientAvailableBalanceException;
import com.service.payment.exception.InsufficientReserveBalanceException;
import com.service.payment.model.Order;
import com.service.payment.model.OrderStatus;
import com.service.payment.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CustomerBalanceValidatorTest {

    private CustomerBalanceValidator customerBalanceValidator;

    @BeforeEach
    public void setUp() {
        this.customerBalanceValidator = new CustomerBalanceValidator();
    }

    @Test
    public void validateReservationCreation_whenCustomerAndOrderAreNull_throwIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> customerBalanceValidator.validateReservationCreation(null, null));
    }

    @Test
    public void validateReservationRollback_whenCustomerAndOrderAreNull_throwIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> customerBalanceValidator.validateReservationRollback(null, null));
    }

    @Test
    public void validateReservationConfirmation_whenCustomerAndOrderAreNull_throwIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> customerBalanceValidator.validateReservationConfirmation(null, null));
    }

    @Test
    public void validateReservationCreation_whenValid_doNotThrowAnyExceptions() {
        Customer customer = new Customer(1L, "testName", 100L, 0L);

        Product product = new Product();
        product.setId(1L);
        product.setQuantity(1);
        product.setPrice(50L);

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.NEW);
        order.setSource("OnlineTestShop");
        order.setCustomerId(customer.getId());
        order.setProduct(product);

        assertDoesNotThrow(() -> customerBalanceValidator.validateReservationCreation(customer, order));
    }

    @Test
    public void validateReservationCreation_whenInvalid_throwInsufficientAvailableBalanceException() {
        Customer customer = new Customer(1L, "testName", 100L, 0L);

        Product product = new Product();
        product.setId(1L);
        product.setQuantity(1);
        product.setPrice(500L);

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.NEW);
        order.setSource("OnlineTestShop");
        order.setCustomerId(customer.getId());
        order.setProduct(product);

        assertThrows(InsufficientAvailableBalanceException.class,
                () -> customerBalanceValidator.validateReservationCreation(customer, order));
    }

    @Test
    public void validateReservationRollback_whenValid_doNotThrowAnyExceptions() {
        Customer customer = new Customer(1L, "testName", 100L, 50L);

        Product product = new Product();
        product.setId(1L);
        product.setQuantity(1);
        product.setPrice(50L);

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.NEW);
        order.setSource("OnlineTestShop");
        order.setCustomerId(customer.getId());
        order.setProduct(product);

        assertDoesNotThrow(() -> customerBalanceValidator.validateReservationRollback(customer, order));
    }

    @Test
    public void validateReservationRollback_whenInvalid_throwInsufficientReserveBalanceException() {
        Customer customer = new Customer(1L, "testName", 100L, 0L);

        Product product = new Product();
        product.setId(1L);
        product.setQuantity(1);
        product.setPrice(500L);

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.NEW);
        order.setSource("OnlineTestShop");
        order.setCustomerId(customer.getId());
        order.setProduct(product);

        assertThrows(InsufficientReserveBalanceException.class,
                () -> customerBalanceValidator.validateReservationRollback(customer, order));
    }

    @Test
    public void validateReservationConfirmation_whenValid_doNotThrowAnyExceptions() {
        Customer customer = new Customer(1L, "testName", 100L, 50L);

        Product product = new Product();
        product.setId(1L);
        product.setQuantity(1);
        product.setPrice(50L);

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.NEW);
        order.setSource("OnlineTestShop");
        order.setCustomerId(customer.getId());
        order.setProduct(product);

        assertDoesNotThrow(() -> customerBalanceValidator.validateReservationConfirmation(customer, order));
    }

    @Test
    public void validateReservationConfirmation_whenInvalid_throwInsufficientReserveBalanceException() {
        Customer customer = new Customer(1L, "testName", 100L, 0L);

        Product product = new Product();
        product.setId(1L);
        product.setQuantity(1);
        product.setPrice(500L);

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.NEW);
        order.setSource("OnlineTestShop");
        order.setCustomerId(customer.getId());
        order.setProduct(product);

        assertThrows(InsufficientReserveBalanceException.class,
                () -> customerBalanceValidator.validateReservationConfirmation(customer, order));
    }


}
