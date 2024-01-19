package com.service.payment.service;

import com.domain.avro.model.AvroOrder;
import com.service.payment.converter.ConverterService;
import com.service.payment.exception.ServiceException;
import com.service.payment.model.Order;
import com.service.payment.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class OrderServiceTest {

    private OrderService orderService;

    @Mock
    private CustomerService customerService;

    @Mock
    private KafkaPaymentOrderProducerService kafkaPaymentOrderProducerService;

    @Mock
    private ConverterService converterService;

    @BeforeEach
    public void setUp(){
        this.orderService = new OrderService(customerService, kafkaPaymentOrderProducerService, converterService);
    }

    @ParameterizedTest
    @NullSource
    public void processNewOrder_whenAvroOrderIsNull_throwIllegalArgumentException(AvroOrder nullAvroOrder){
        assertThrows(IllegalArgumentException.class, () -> orderService.processNewOrder(nullAvroOrder));
    }

    @ParameterizedTest
    @NullSource
    public void processRollbackOrder_whenAvroOrderIsNull_throwIllegalArgumentException(AvroOrder nullAvroOrder){
        assertThrows(IllegalArgumentException.class, () -> orderService.processRollbackOrder(nullAvroOrder));
    }

    @ParameterizedTest
    @NullSource
    public void processConfirmationOrder_whenAvroOrderIsNull_throwIllegalArgumentException(AvroOrder nullAvroOrder){
        assertThrows(IllegalArgumentException.class, () -> orderService.processConfirmationOrder(nullAvroOrder));
    }

    @Test
    public void processNewOrder_whenCustomerServiceThrowsServiceException_setOrderStatusToRejectAndDoNotThrowAnyException(){
        AvroOrder avroOrder = new AvroOrder();
        Order order = new Order();
        order.setStatus(OrderStatus.NEW);

        when(converterService.convert(avroOrder, Order.class)).thenReturn(order);
        doThrow(ServiceException.class).when(customerService).makeReservation(order);

        assertDoesNotThrow(() -> orderService.processNewOrder(avroOrder));
        assertEquals(order.getStatus(), OrderStatus.REJECT);

        verify(converterService).convert(avroOrder, Order.class);
        verify(customerService).makeReservation(order);
        verify(kafkaPaymentOrderProducerService).sendOrder(order);
    }

    @Test
    public void processNewOrder_whenCustomerServiceDoesNotThrowServiceException_setOrderStatusToAccept(){
        AvroOrder avroOrder = new AvroOrder();
        Order order = new Order();
        order.setStatus(OrderStatus.NEW);

        when(converterService.convert(avroOrder, Order.class)).thenReturn(order);

        assertDoesNotThrow(() -> orderService.processNewOrder(avroOrder));
        assertEquals(order.getStatus(), OrderStatus.ACCEPT);

        verify(converterService).convert(avroOrder, Order.class);
        verify(customerService).makeReservation(order);
        verify(kafkaPaymentOrderProducerService).sendOrder(order);
    }

    @Test
    public void processRollbackOrder_whenAllIsFine(){
        AvroOrder avroOrder = new AvroOrder();
        Order order = new Order();
        order.setStatus(OrderStatus.NEW);

        when(converterService.convert(avroOrder, Order.class)).thenReturn(order);

        assertDoesNotThrow(() -> orderService.processRollbackOrder(avroOrder));

        verify(converterService).convert(avroOrder, Order.class);
        verify(customerService).rollbackReservation(order);
        verifyNoInteractions(kafkaPaymentOrderProducerService);
    }

    @Test
    public void processRollbackOrder_whenCustomerServiceThrowServiceException_doesNotThrowsException(){
        AvroOrder avroOrder = new AvroOrder();
        Order order = new Order();
        order.setStatus(OrderStatus.NEW);

        when(converterService.convert(avroOrder, Order.class)).thenReturn(order);
        doThrow(ServiceException.class).when(customerService).rollbackReservation(order);

        assertDoesNotThrow(() -> orderService.processRollbackOrder(avroOrder));

        verify(converterService).convert(avroOrder, Order.class);
        verify(customerService).rollbackReservation(order);
        verifyNoInteractions(kafkaPaymentOrderProducerService);
    }

    @Test
    public void processConfirmationOrder_whenAllIsFine(){
        AvroOrder avroOrder = new AvroOrder();
        Order order = new Order();
        order.setStatus(OrderStatus.NEW);

        when(converterService.convert(avroOrder, Order.class)).thenReturn(order);
        doThrow(ServiceException.class).when(customerService).confirmReservation(order);

        assertDoesNotThrow(() -> orderService.processConfirmationOrder(avroOrder));

        verify(converterService).convert(avroOrder, Order.class);
        verify(customerService).confirmReservation(order);
        verifyNoInteractions(kafkaPaymentOrderProducerService);
    }


}
