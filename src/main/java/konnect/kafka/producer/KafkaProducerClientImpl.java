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
    KafkaProducer<String, Object> kafkaProducer;

    private final AppConfig kafkaConfig;
    private final String kafkaTopic;

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerClientImpl.class);
    private static final String KAFKA_ERROR_RECORD_STR_TEMPLATE = "Error occurred, while producing event to kafka. " +
            "EventKey:- {}, EventValue:- {}";

    public KafkaProducerClientImpl(final AppConfig kafkaConfig) {
        this.kafkaConfig = kafkaConfig;
        this.kafkaTopic = kafkaConfig.getKafkaTopicName();
        setProperties();
        this.kafkaProducer = new KafkaProducer<>(kafkaProps);
    }

    private void setProperties() {
        kafkaProps = new Properties();
        kafkaProps.put(BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getKafkaProducerBootstrapServers());
        kafkaProps.put(KEY_SERIALIZER_CLASS_CONFIG, kafkaConfig.getKafkaProducerKeySerializer());
        kafkaProps.put(VALUE_SERIALIZER_CLASS_CONFIG, kafkaConfig.getKafkaProducerValueSerializer());
        kafkaProps.put(BATCH_SIZE_CONFIG, kafkaConfig.getKafkaProducerBatchSize());
    }

    @Override
    public void sendDataSync(final Object value) {
        ProducerRecord<String, Object> event = new ProducerRecord<>(kafkaTopic, null, value);
        try {
            kafkaProducer.send(event).get();
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
        ProducerRecord<String, Object> event = new ProducerRecord<>(kafkaTopic, null, value);
        try {
            kafkaProducer.send(event, new KafkaProducerCallback(null, value.toString()));
        } catch (final Exception ex) {
            LOGGER.error(KAFKA_ERROR_RECORD_STR_TEMPLATE,
                    "null",
                    value.toString(),
                    ex);
        }
    }

    @Override
    public void sendData(final Object value) {
        ProducerRecord<String, Object> event = new ProducerRecord<>(kafkaTopic, null, value);
        try {
            kafkaProducer.send(event);
            kafkaProducer.flush();
        } catch (final Exception ex) {
            LOGGER.error(KAFKA_ERROR_RECORD_STR_TEMPLATE,
                    "null",
                    value.toString(),
                    ex);
        }
    }
}
