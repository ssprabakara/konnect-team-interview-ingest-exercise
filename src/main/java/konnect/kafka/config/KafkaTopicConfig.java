package konnect.kafka.config;

public interface KafkaTopicConfig {
    String DEFAULT_TOPIC_NAME = "cdc-events";

    /**
     *
     */
    default String getTopicName() {
        return DEFAULT_TOPIC_NAME;
    }
}
