package konnect.kafka.producer;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KProducerCallback implements Callback {
    private static final Logger LOGGER = LoggerFactory.getLogger(KProducerCallback.class);
    @Override
    public void onCompletion(final RecordMetadata recordMetadata, final Exception ex) {
        if (ex != null) {
            LOGGER.error("Error occurred:- {}", ex.getMessage());
            ex.printStackTrace();
        } else {
            LOGGER.info("Produced record to topic {} partition {} @offset {}}",
                    recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset());
        }
    }
}
