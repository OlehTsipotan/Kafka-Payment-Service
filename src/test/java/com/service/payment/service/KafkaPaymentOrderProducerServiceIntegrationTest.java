package com.service.payment.service;

import com.service.avro.model.AvroOrder;
import com.service.avro.model.AvroProduct;
import com.service.payment.container.SchemaRegistryContainer;
import com.service.payment.converter.OrderToAvroOrderConverter;
import com.service.payment.model.Order;
import com.service.payment.model.OrderStatus;
import com.service.payment.model.Product;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.specific.SpecificData;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.rnorth.ducttape.unreliables.Unreliables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@Import(com.service.payment.service.KafkaPaymentOrderProducerServiceIntegrationTest.KafkaTestContainersConfiguration.class)
@SpringBootTest(classes = KafkaPaymentOrderProducerService.class)
@DirtiesContext
public class KafkaPaymentOrderProducerServiceIntegrationTest {

    public static final String CONFLUENT_PLATFORM_VERSION = "7.4.0";

    private static final Network KAFKA_NETWORK = Network.newNetwork();
    private static final DockerImageName KAFKA_IMAGE =
            DockerImageName.parse("confluentinc/cp-kafka").withTag(CONFLUENT_PLATFORM_VERSION);
    private static final KafkaContainer KAFKA =
            new KafkaContainer(KAFKA_IMAGE).withNetwork(KAFKA_NETWORK).withExposedPorts(9093).withKraft();

    private static final SchemaRegistryContainer SCHEMA_REGISTRY =
            new SchemaRegistryContainer(CONFLUENT_PLATFORM_VERSION);

    @Autowired
    private KafkaPaymentOrderProducerService producer;

    @Autowired
    private KafkaConsumer<String, GenericRecord> consumer;

    @BeforeAll
    static void startKafkaContainer() {
        KAFKA.start();
        SCHEMA_REGISTRY.withKafka(KAFKA).start();
    }

    @AfterAll
    static void stopKafkaContainer() {
        SCHEMA_REGISTRY.stop();
        KAFKA.stop();
    }

    @NotNull
    private static Order getOrder() {
        UUID id = UUID.randomUUID();
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
        order.setId(id);
        order.setStatus(OrderStatus.NEW);
        order.setProduct(product);
        order.setSource(source);
        order.setCustomerId(customerId);
        return order;
    }

    @Test
    public void sendOrder_whenOrderIsSentToKafka_success() {
        String topicName = "payment-orders";
        consumer.subscribe(Collections.singletonList(topicName));

        Order order = getOrder();

        producer.sendOrder(order);

        Unreliables.retryUntilTrue(20, TimeUnit.SECONDS, () -> {
            ConsumerRecords<String, GenericRecord> records = consumer.poll(Duration.ofMillis(100));
            if (records.isEmpty()) {
                return false;
            }
            for (ConsumerRecord<String, GenericRecord> record : records) {
                AvroOrder receivedOrder =
                        (AvroOrder) SpecificData.get().deepCopy(record.value().getSchema(), record.value());
                assertNotNull(receivedOrder);

                assertEquals(order.getId().toString(), receivedOrder.getId().toString());
                assertEquals(order.getCustomerId(), receivedOrder.getCustomerId());
                assertEquals(order.getStatus().toString(), receivedOrder.getStatus().toString());
                assertEquals(order.getSource(), receivedOrder.getSource().toString());

                AvroProduct avroProduct = receivedOrder.getProduct();
                assertNotNull(avroProduct);
                assertEquals(order.getProduct().getId(), avroProduct.getId());
                assertEquals(order.getProduct().getQuantity(), avroProduct.getQuantity());
                assertEquals(order.getProduct().getPrice(), avroProduct.getPrice());
            }
            return true;
        });
        consumer.unsubscribe();
    }

    @TestConfiguration
    static class KafkaTestContainersConfiguration {

        @Bean
        public KafkaConsumer<String, GenericRecord> kafkaConsumer() {
            Properties props = new Properties();

            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers());
            props.put(ConsumerConfig.GROUP_ID_CONFIG, "group");

            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
            props.put("schema.registry.url",
                    "http://" + SCHEMA_REGISTRY.getHost() + ":" + SCHEMA_REGISTRY.getFirstMappedPort());

            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

            return new KafkaConsumer<>(props);
        }

        @Bean
        public ProducerFactory<String, AvroOrder> producerFactory() {
            Map<String, Object> configProps = new HashMap<>();
            configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers());
            configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
            configProps.put("schema.registry.url",
                    "http://" + SCHEMA_REGISTRY.getHost() + ":" + SCHEMA_REGISTRY.getFirstMappedPort());
            return new DefaultKafkaProducerFactory<>(configProps);
        }

        @Bean
        public KafkaTemplate<String, AvroOrder> kafkaTemplate() {
            return new KafkaTemplate<>(producerFactory());
        }

        @Bean
        public OrderToAvroOrderConverter orderToAvroOrderConverter() {
            return new OrderToAvroOrderConverter();
        }

    }
}
