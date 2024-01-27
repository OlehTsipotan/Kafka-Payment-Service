package com.service.payment.service;

import com.service.avro.model.AvroOrder;
import com.service.avro.model.AvroOrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class OrderProcessingServiceTest {

    private OrderProcessingService orderProcessingService;

    @Mock
    private OrderService orderService;

    @BeforeEach
    public void setUp(){
        this.orderProcessingService = new OrderProcessingService(orderService);
    }

    @ParameterizedTest
    @NullSource
    public void process_whenAvroOrderIsNull_throwIllegalArgumentException(AvroOrder nullAvroOrder){
        assertThrows(IllegalArgumentException.class, () -> orderProcessingService.process(nullAvroOrder));
    }

    @Test
    public void process_whenAvroOrderStatusIsNew_callOrderServiceProcessNewOrder(){
        AvroOrder avroOrder = new AvroOrder();
        avroOrder.setStatus(AvroOrderStatus.NEW);

        assertDoesNotThrow(() -> orderProcessingService.process(avroOrder));

        verify(orderService).processNewOrder(avroOrder);
        verifyNoMoreInteractions(orderService);
    }

    @Test
    public void process_whenAvroOrderStatusIsRollback_callOrderServiceProcessRollbackOrder(){
        AvroOrder avroOrder = new AvroOrder();
        avroOrder.setStatus(AvroOrderStatus.ROLLBACK);

        assertDoesNotThrow(() -> orderProcessingService.process(avroOrder));

        verify(orderService).processRollbackOrder(avroOrder);
        verifyNoMoreInteractions(orderService);
    }

    @Test
    public void process_whenAvroOrderStatusIsConfirmation_callOrderServiceProcessConfirmationOrder(){
        AvroOrder avroOrder = new AvroOrder();
        avroOrder.setStatus(AvroOrderStatus.CONFIRMATION);

        assertDoesNotThrow(() -> orderProcessingService.process(avroOrder));

        verify(orderService).processConfirmationOrder(avroOrder);
        verifyNoMoreInteractions(orderService);
    }

    @Test
    public void process_whenAvroOrderStatusIsNull_doesNotThrowAnyExceptionsAndDoesNotCallAnyOrderServiceMethod(){
        AvroOrder avroOrder = new AvroOrder();

        assertDoesNotThrow(() -> orderProcessingService.process(avroOrder));

        verifyNoInteractions(orderService);
    }

    @Test
    public void process_whenUnknownOrderStatus_doesNotThrowAnyExceptionsAndDoesNotCallAnyOrderServiceMethod(){
        AvroOrder avroOrder = new AvroOrder();
        avroOrder.setStatus(AvroOrderStatus.ACCEPT);

        assertDoesNotThrow(() -> orderProcessingService.process(avroOrder));

        verifyNoInteractions(orderService);
    }

}
