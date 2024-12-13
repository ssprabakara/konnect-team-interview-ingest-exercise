package konnect.kafka.producer;

import konnect.config.AppConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.BATCH_SIZE_CONFIG;

public class KafkaProducerClientImpl implements KafkaProducerClient {
    Properties kafkaProps;
    KafkaProducer<String, Object> producer; // Object generic ???

    private final AppConfig kafkaConfig;
    private final String kafkaTopic;

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerClientImpl.class);
    private static final String KAFKA_ERROR_RECORD_STR_TEMPLATE = "Error occurred, while producing event to kafka. " +
            "EventKey:- {}, EventValue:- {}";

    public KafkaProducerClientImpl(final AppConfig kafkaConfig) {
        this.kafkaConfig = kafkaConfig;
        this.kafkaTopic = kafkaConfig.getTopicName();
        setProperties();
        buildProducer();
    }

    private void setProperties() {
        kafkaProps = new Properties();
        kafkaProps.put(BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getProducerBootstrapServers());
        kafkaProps.put(KEY_SERIALIZER_CLASS_CONFIG, kafkaConfig.getKeySerializer());
        kafkaProps.put(VALUE_SERIALIZER_CLASS_CONFIG, kafkaConfig.getValueSerializer());
        kafkaProps.put(BATCH_SIZE_CONFIG, kafkaConfig.getBatchSize());
    }

    private void buildProducer() {
        producer = new KafkaProducer<>(kafkaProps);
    }

    @Override
    public void sendDataSync(final Object value) {
        ProducerRecord<String, Object> event =
                new ProducerRecord<>(kafkaTopic, null, value);
        try {
            producer = new KafkaProducer<>(kafkaProps);
            producer.send(event).get();
        } catch (final Exception ex) {
            LOGGER.error(KAFKA_ERROR_RECORD_STR_TEMPLATE,
                    "null",
                    value.toString(),
                    ex);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void sendDataAsync(final Object value) {
        ProducerRecord<String, Object> event =
                new ProducerRecord<>(kafkaTopic, null, value);
        try {
            producer.send(event, new KafkaProducerCallback(null, value.toString()));
        } catch (final Exception ex) {
            LOGGER.error(KAFKA_ERROR_RECORD_STR_TEMPLATE,
                    "null",
                    value.toString(),
                    ex);
        }
    }

    // fire and forget style
    @Override
    public void sendData(final Object value) {
        ProducerRecord<String, Object> event =
                new ProducerRecord<>(kafkaTopic, null, value);
        try {
            producer.send(event);
            producer.flush();
        } catch (final Exception ex) {
            LOGGER.error(KAFKA_ERROR_RECORD_STR_TEMPLATE,
                    "null",
                    value.toString(),
                    ex);
        }
    }
}
