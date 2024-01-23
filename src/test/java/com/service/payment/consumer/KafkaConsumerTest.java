package com.service.payment.consumer;

import com.domain.avro.model.AvroOrder;
import com.domain.avro.model.AvroOrderStatus;
import com.domain.avro.model.AvroProduct;
import com.service.payment.container.SchemaRegistryContainer;
import com.service.payment.service.OrderProcessingService;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@Import(com.service.payment.consumer.KafkaConsumerTest.KafkaTestContainersConfiguration.class)
@SpringBootTest(classes = com.service.payment.consumer.KafkaConsumer.class)
@DirtiesContext
public class KafkaConsumerTest {

    public static final String CONFLUENT_PLATFORM_VERSION = "7.4.0";

    private static final Network KAFKA_NETWORK = Network.newNetwork();
    private static final DockerImageName KAFKA_IMAGE =
            DockerImageName.parse("confluentinc/cp-kafka").withTag(CONFLUENT_PLATFORM_VERSION);
    private static final KafkaContainer KAFKA =
            new KafkaContainer(KAFKA_IMAGE).withNetwork(KAFKA_NETWORK).withExposedPorts(9093).withKraft()
                    .withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "true");

    private static final SchemaRegistryContainer SCHEMA_REGISTRY =
            new SchemaRegistryContainer(CONFLUENT_PLATFORM_VERSION);

    @Autowired
    private OrderProcessingService orderProcessingService;

    @Autowired
    private KafkaProducer<String, AvroOrder> producer;

    @BeforeAll
    static void startKafkaContainer() {
        KAFKA.start();
        SCHEMA_REGISTRY.withKafka(KAFKA).start();
    }

    @NotNull
    private static AvroOrder getAvroOrder() {
        String id = UUID.randomUUID().toString();
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
        avroOrder.setId(id);
        avroOrder.setStatus(AvroOrderStatus.NEW);
        avroOrder.setProduct(avroProduct);
        avroOrder.setSource(source);
        avroOrder.setCustomerId(customerId);
        return avroOrder;
    }

    @Test
    public void sendOrder_whenOrderIsSentToKafka_success() throws InterruptedException {
        String topicName = "orders";

        AvroOrder avroOrder = getAvroOrder();

        producer.send(new ProducerRecord<>(topicName, avroOrder.getId().toString(), avroOrder));

        Thread.sleep(3000);
        verify(orderProcessingService, times(1)).process(any());
    }

    @TestConfiguration
    static class KafkaTestContainersConfiguration {

        @Bean
        public Map<String, Object> consumerConfigs() {
            Map<String, Object> props = new HashMap<>();
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers());
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            props.put(ConsumerConfig.GROUP_ID_CONFIG, "groupId");
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
            props.put("specific.avro.reader", true);
            props.put("schema.registry.url",
                    "http://" + SCHEMA_REGISTRY.getHost() + ":" + SCHEMA_REGISTRY.getFirstMappedPort());
            return props;
        }

        @Bean
        public Map<String, Object> producerConfig() {
            Map<String, Object> configProps = new HashMap<>();
            configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers());
            configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
            configProps.put("schema.registry.url",
                    "http://" + SCHEMA_REGISTRY.getHost() + ":" + SCHEMA_REGISTRY.getFirstMappedPort());
            return configProps;
        }

        @Bean
        public ConsumerFactory<String, AvroOrder> consumerFactory() {
            return new DefaultKafkaConsumerFactory<>(consumerConfigs());
        }

        @Bean
        public KafkaProducer<String, AvroOrder> kafkaProducer() {
            return new KafkaProducer<>(producerConfig());
        }

        @Bean
        public OrderProcessingService orderProcessingService() {
            return Mockito.mock(OrderProcessingService.class);
        }

        @Bean
        public ConcurrentKafkaListenerContainerFactory<String, AvroOrder> kafkaListenerContainerFactory() {

            ConcurrentKafkaListenerContainerFactory<String, AvroOrder> factory =
                    new ConcurrentKafkaListenerContainerFactory<>();
            factory.setConsumerFactory(consumerFactory());

            return factory;
        }

    }
}
