package konnect.kafka.producer;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.BATCH_SIZE_CONFIG;

public class KProducer {
    Properties kafkaProps;
    KafkaProducer<String, JsonNode> producer;
    private static final Logger LOGGER = LoggerFactory.getLogger(KProducer.class);
    public static final String KAFKA_TOPIC = "cdc-events";

    public KProducer() {
        setProperties();
        buildProducer();
    }

    private void setProperties() {
        kafkaProps = new Properties();
        kafkaProps.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        kafkaProps.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        kafkaProps.put(VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());
        kafkaProps.put(BATCH_SIZE_CONFIG, 1024 * 1024);
    }

    private void buildProducer() {
        producer = new KafkaProducer<>(kafkaProps);
    }

    public void sendDataSync(final JsonNode value) {
        ProducerRecord<String, JsonNode> event =
                new ProducerRecord<>(KAFKA_TOPIC, null, value);
        try {
            producer = new KafkaProducer<>(kafkaProps);
            producer.send(event).get();
        } catch (final Exception ex) {
            LOGGER.error("Error occurred in sendDataSync:-  {}", ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    public void sendDataASync(final JsonNode value) {
        ProducerRecord<String, JsonNode> event =
                new ProducerRecord<>(KAFKA_TOPIC, null, value);
        try {
            producer.send(event, new KProducerCallback());
        } catch (final Exception ex) {
            LOGGER.error("Error occurred in sendDataASync:-  {}", ex.getMessage());
        }
    }

    // fire and forget style
    public void sendData(final JsonNode value) {

        ProducerRecord<String, JsonNode> event =
                new ProducerRecord<>(KAFKA_TOPIC, null, value);
        try {
            producer.send(event);
            producer.flush();
        } catch (final Exception ex) {
            LOGGER.error("Error occurred in sendData:-  {}", ex.getMessage());
        }
    }
}
