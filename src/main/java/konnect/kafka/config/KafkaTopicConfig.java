package konnect.kafka.config;

public interface KafkaTopicConfig {
    String DEFAULT_TOPIC_NAME = "cdc-events";

    /**
     * Default topic, that will be used for producer and consumer.
     *
     * @returns The topic name value
     */
    default String getKafkaTopicName() {
        return DEFAULT_TOPIC_NAME;
    }
}
