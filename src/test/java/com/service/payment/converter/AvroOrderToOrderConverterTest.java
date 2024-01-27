package com.service.payment.converter;

import com.service.avro.model.AvroOrder;
import com.service.avro.model.AvroOrderStatus;
import com.service.avro.model.AvroProduct;
import com.service.payment.model.Order;
import com.service.payment.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AvroOrderToOrderConverterTest {

    private AvroOrderToOrderConverter converter;

    @BeforeEach
    public void setUp() {
        converter = new AvroOrderToOrderConverter();
    }

    @ParameterizedTest
    @NullSource
    public void convert_whenOrderIsNull_throwIllegalArgumentException(AvroOrder avroOrder) {
        assertThrows(IllegalArgumentException.class, () -> converter.convert(avroOrder));
    }

    @Test
    public void convert_whenOrderFieldsAreNull_success() {
        AvroOrder avroOrder = new AvroOrder();


        Order order = converter.convert(avroOrder);

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
        String uuidToSet = UUID.randomUUID().toString();
        Long customerId = 1L;
        String source = "Source";
        Long productId = 1L;
        Integer productQuantity = 1;
        Long productPrice = 100L;

        AvroProduct avroProduct = new AvroProduct();
        avroProduct.setId(productId);
        avroProduct.setQuantity(productQuantity);
        avroProduct.setPrice(productPrice);

        AvroOrder avroOrder = new AvroOrder();
        avroOrder.setId(uuidToSet);
        avroOrder.setStatus(AvroOrderStatus.NEW);
        avroOrder.setProduct(avroProduct);
        avroOrder.setSource(source);
        avroOrder.setCustomerId(customerId);

        Order order = converter.convert(avroOrder);

        assertNotNull(avroOrder);
        Product product = order.getProduct();

        assertNotNull(product);
        assertEquals(order.getId().toString(), avroOrder.getId().toString());
        assertEquals(order.getCustomerId(), avroOrder.getCustomerId());
        assertEquals(order.getStatus().toString(), avroOrder.getStatus().toString());
        assertEquals(order.getSource(), avroOrder.getSource());
        assertEquals(product.getId(), productId);
        assertEquals(product.getPrice(), productPrice);
        assertEquals(product.getQuantity(), productQuantity);
    }

}

