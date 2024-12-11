package konnect.kafka.producer;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record KafkaProducerCallback(String messageKey, String messageValue) implements Callback {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerCallback.class);

    @Override
    public void onCompletion(final RecordMetadata recordMetadata, final Exception ex) {
        if (ex != null) {
            LOGGER.error("Error occurred, while producing record to Kafka. MessageKey:- {}, MessageValue:- {}",
                    messageKey == null ? "null" : messageKey,
                    messageValue,
                    ex);
            throw new KafkaException("Async Write failure:- ", ex);
        } else {
            LOGGER.info("Pushed record to Kafka Topic:- {},  Partition:- {},  @Offset:- {}, Timestamp:- {}",
                    recordMetadata.topic(),
                    recordMetadata.partition(),
                    recordMetadata.offset(),
                    recordMetadata.timestamp());
        }
    }
}
