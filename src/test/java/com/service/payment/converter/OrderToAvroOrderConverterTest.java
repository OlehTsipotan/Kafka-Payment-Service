package com.service.payment.converter;

import com.domain.avro.model.AvroOrder;
import com.domain.avro.model.AvroProduct;
import com.service.payment.model.Order;
import com.service.payment.model.OrderStatus;
import com.service.payment.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class OrderToAvroOrderConverterTest {

    private OrderToAvroOrderConverter converter;

    @BeforeEach
    public void setUp() {
        converter = new OrderToAvroOrderConverter();
    }

    @ParameterizedTest
    @NullSource
    public void convert_whenOrderIsNull_throwIllegalArgumentException(Order order) {
        assertThrows(IllegalArgumentException.class, () -> converter.convert(order));
    }

    @Test
    public void convert_whenOrderFieldsAreNull_success() {
        Order order = new Order();


        AvroOrder avroOrder = converter.convert(order);

        assertNotNull(avroOrder);
        AvroProduct avroProduct = avroOrder.getProduct();
        assertNull(avroProduct);
        assertEquals(avroOrder.getId(), order.getId());
        assertEquals(avroOrder.getCustomerId(), order.getCustomerId());
        assertEquals(avroOrder.getStatus(), order.getStatus());
        assertEquals(avroOrder.getSource(), order.getSource());
    }

    @Test
    public void convert_success() {
        UUID uuidToSet = UUID.randomUUID();
        Long customerId = 1L;
        String source = "Source";
        Long productId = 1L;
        Integer productQuantity = 1;
        Long productPrice = 100L;

        Product product = new Product();
        product.setId(productId);
        product.setQuantity(productQuantity);
        product.setPrice(productPrice);

        Order order = new Order();
        order.setId(uuidToSet);
        order.setStatus(OrderStatus.NEW);
        order.setProduct(product);
        order.setSource(source);
        order.setCustomerId(customerId);

        AvroOrder avroOrder = converter.convert(order);

        assertNotNull(avroOrder);
        AvroProduct avroProduct = avroOrder.getProduct();

        assertNotNull(avroProduct);
        assertEquals(avroOrder.getId().toString(), order.getId().toString());
        assertEquals(avroOrder.getCustomerId(), order.getCustomerId());
        assertEquals(avroOrder.getStatus().toString(), order.getStatus().toString());
        assertEquals(avroOrder.getSource(), order.getSource());
        assertEquals(avroProduct.getId(), productId);
        assertEquals(avroProduct.getPrice(), productPrice);
        assertEquals(avroProduct.getQuantity(), productQuantity);
    }

}
