package konnect.config;

import java.util.Collections;
import java.util.List;
import konnect.exception.ConfigNotFoundException;
import konnect.httpclient.config.HttpClientConfig;
import konnect.kafka.config.KafkaConsumerConfig;
import konnect.kafka.config.KafkaProducerConfig;
import konnect.kafka.config.KafkaTopicConfig;
import konnect.opensearch.config.OpensearchConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static konnect.common.Constants.HTTPCLIENT_CONNECT_TIMEOUT;
import static konnect.common.Constants.HTTPCLIENT_KEEP_ALIVE_DURATION;
import static konnect.common.Constants.HTTPCLIENT_MAX_IDLE_CONNECTIONS;
import static konnect.common.Constants.HTTPCLIENT_READ_TIMEOUT;
import static konnect.common.Constants.HTTPCLIENT_WRITE_TIMEOUT;
import static konnect.common.Constants.KAFKA_CONSUMER_ALLOW_AUTO_CREATE_TOPICS;
import static konnect.common.Constants.KAFKA_CONSUMER_AUTO_COMMIT_INTERVAL_IN_MS;
import static konnect.common.Constants.KAFKA_CONSUMER_AUTO_OFFSET_RESET;
import static konnect.common.Constants.KAFKA_CONSUMER_BOOTSTRAP_SERVERS;
import static konnect.common.Constants.KAFKA_CONSUMER_CONNECTION_MAX_IDLE_IN_MS;
import static konnect.common.Constants.KAFKA_CONSUMER_ENABLE_AUTO_COMMIT;
import static konnect.common.Constants.KAFKA_CONSUMER_GROUP_ID;
import static konnect.common.Constants.KAFKA_CONSUMER_ISOLATION_LEVEL;
import static konnect.common.Constants.KAFKA_CONSUMER_KEY_DESERIALIZER;
import static konnect.common.Constants.KAFKA_CONSUMER_MAX_POLL_INTERVAL_IN_MS;
import static konnect.common.Constants.KAFKA_CONSUMER_MAX_POLL_RECORDS;
import static konnect.common.Constants.KAFKA_CONSUMER_RECEIVER_BUFFER_BYTES;
import static konnect.common.Constants.KAFKA_CONSUMER_REQUEST_TIMEOUT_IN_MS;
import static konnect.common.Constants.KAFKA_CONSUMER_SEND_BUFFER_BYTES;
import static konnect.common.Constants.KAFKA_CONSUMER_SESSION_TIMEOUT_IN_MS;
import static konnect.common.Constants.KAFKA_CONSUMER_VALUE_DESERIALIZER;
import static konnect.common.Constants.KAFKA_PRODUCER_ACKS;
import static konnect.common.Constants.KAFKA_PRODUCER_BATCH_SIZE;
import static konnect.common.Constants.KAFKA_PRODUCER_BOOTSTRAP_SERVERS;
import static konnect.common.Constants.KAFKA_PRODUCER_BUFFER_MEMORY;
import static konnect.common.Constants.KAFKA_PRODUCER_CONNECTION_MAX_IDLE_TIME_IN_MS;
import static konnect.common.Constants.KAFKA_PRODUCER_DELIVERY_TIMEOUT_IN_MS;
import static konnect.common.Constants.KAFKA_PRODUCER_ENABLE_IDEMPOTENCE;
import static konnect.common.Constants.KAFKA_PRODUCER_KEY_SERIALIZER;
import static konnect.common.Constants.KAFKA_PRODUCER_LINGER_IN_MS;
import static konnect.common.Constants.KAFKA_PRODUCER_MAX_BLOCK_IN_MS;
import static konnect.common.Constants.KAFKA_PRODUCER_MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION;
import static konnect.common.Constants.KAFKA_PRODUCER_RECEIVE_BUFFER_BYTES;
import static konnect.common.Constants.KAFKA_PRODUCER_REQUEST_TIMEOUT_IN_MS;
import static konnect.common.Constants.KAFKA_PRODUCER_RETRIES;
import static konnect.common.Constants.KAFKA_PRODUCER_SEND_BUFFER_BYTES;
import static konnect.common.Constants.KAFKA_PRODUCER_VALUE_SERIALIZER;
import static konnect.common.Constants.KAFKA_TOPIC_TOPIC_NAME;
import static konnect.common.Constants.OPENSEARCH_BULK_POST_REQUEST_URL;
import static konnect.common.Constants.OPENSEARCH_INDEX_NAME;
import static konnect.common.Constants.OPENSEARCH_POST_REQUEST_URL;

public class AppConfig implements HttpClientConfig,
                                  KafkaConsumerConfig,
                                  KafkaProducerConfig,
                                  KafkaTopicConfig,
                                  OpensearchConfig {

    private final ConfigReader configReader;
    private static final Logger LOGGER = LoggerFactory.getLogger(AppConfig.class);

    public AppConfig(final ConfigReader configReader) {
        this.configReader = configReader;
    }

    // HTTP Client configs

    public int getConnectTimeoutInSeconds() {
        try {
            return configReader.getIntValue(HTTPCLIENT_CONNECT_TIMEOUT);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return HttpClientConfig.super.getConnectTimeoutInSeconds();
    }

    public int getKeepAliveDurationInMinutes() {
        try {
            return configReader.getIntValue(HTTPCLIENT_KEEP_ALIVE_DURATION);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return HttpClientConfig.super.getKeepAliveDurationInMinutes();
    }

    public int getMaxIdleConnections() {
        try {
            return configReader.getIntValue(HTTPCLIENT_MAX_IDLE_CONNECTIONS);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return HttpClientConfig.super.getMaxIdleConnections();
    }

    public int getReadTimeout() {
        try {
            return configReader.getIntValue(HTTPCLIENT_READ_TIMEOUT);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return HttpClientConfig.super.getReadTimeout();
    }

    public int getWriteTimeout() {
        try {
            return configReader.getIntValue(HTTPCLIENT_WRITE_TIMEOUT);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return HttpClientConfig.super.getWriteTimeout();
    }

    // Kafka Topic configs

    @Override
    public String getTopicName() {
        try {
            return configReader.getValue(KAFKA_TOPIC_TOPIC_NAME);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaTopicConfig.super.getTopicName();
    }


    // Kafka Consumer configs

    @Override
    public boolean getAllowAutoCreateTopics() {
        try {
            return configReader.getBooleanValue(KAFKA_CONSUMER_ALLOW_AUTO_CREATE_TOPICS);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return false;
    }

    @Override
    public int getAutoCommitIntervalInMs() {
        try {
            return configReader.getIntValue(KAFKA_CONSUMER_AUTO_COMMIT_INTERVAL_IN_MS);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaConsumerConfig.super.getAutoCommitIntervalInMs();
    }

    @Override
    public String getAutoOffsetReset() {
        try {
            return configReader.getValue(KAFKA_CONSUMER_AUTO_OFFSET_RESET);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaConsumerConfig.super.getAutoOffsetReset();
    }

    @Override
    public long getConnectionMaxIdleInMs() {
        try {
            return configReader.getLongValue(KAFKA_CONSUMER_CONNECTION_MAX_IDLE_IN_MS);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaConsumerConfig.super.getConnectionMaxIdleInMs();
    }

    @Override
    public List<String> getConsumerBootstrapServers() {
        try {
            return configReader.getTextArrayValue(KAFKA_CONSUMER_BOOTSTRAP_SERVERS);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return Collections.emptyList();
    }

    @Override
    public int getConsumerReceiveBufferBytes() {
        try {
            return configReader.getIntValue(KAFKA_CONSUMER_RECEIVER_BUFFER_BYTES);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaConsumerConfig.super.getConsumerReceiveBufferBytes();
    }

    @Override
    public int getConsumerRequestTimeoutInMs() {
        try {
            return configReader.getIntValue(KAFKA_CONSUMER_REQUEST_TIMEOUT_IN_MS);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaConsumerConfig.super.getConsumerRequestTimeoutInMs();
    }

    @Override
    public int getConsumerSendBufferBytes() {
        try {
            return configReader.getIntValue(KAFKA_CONSUMER_SEND_BUFFER_BYTES);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaConsumerConfig.super.getConsumerSendBufferBytes();
    }

    @Override
    public boolean getEnableAutoCommit() {
        try {
            return configReader.getBooleanValue(KAFKA_CONSUMER_ENABLE_AUTO_COMMIT);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaConsumerConfig.super.getEnableAutoCommit();
    }

    @Override
    public String getGroupId() {
        try {
            return configReader.getValue(KAFKA_CONSUMER_GROUP_ID);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return null;
    }

    @Override
    public String getIsolationLevel() {
        try {
            return configReader.getValue(KAFKA_CONSUMER_ISOLATION_LEVEL);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaConsumerConfig.super.getIsolationLevel();
    }

    @Override
    public int getMaxPollIntervalInMs() {
        try {
            return configReader.getIntValue(KAFKA_CONSUMER_MAX_POLL_INTERVAL_IN_MS);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaConsumerConfig.super.getMaxPollIntervalInMs();
    }

    @Override
    public int getMaxPollRecords() {
        try {
            return configReader.getIntValue(KAFKA_CONSUMER_MAX_POLL_RECORDS);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaConsumerConfig.super.getMaxPollRecords();
    }

    @Override
    public String getKeyDeserializer() {
        try {
            return configReader.getValue(KAFKA_CONSUMER_KEY_DESERIALIZER);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaConsumerConfig.super.getKeyDeserializer();
    }

    @Override
    public int getSessionTimeoutInMs() {
        try {
            return configReader.getIntValue(KAFKA_CONSUMER_SESSION_TIMEOUT_IN_MS);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaConsumerConfig.super.getSessionTimeoutInMs();
    }

    @Override
    public String getValueDeserializer() {
        try {
            return configReader.getValue(KAFKA_CONSUMER_VALUE_DESERIALIZER);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaConsumerConfig.super.getValueDeserializer();
    }


    // Kafka Producer Configs

    @Override
    public String getAcks() {
        try {
            return configReader.getValue(KAFKA_PRODUCER_ACKS);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaProducerConfig.super.getAcks();
    }

    @Override
    public int getBatchSize() {
        try {
            return configReader.getIntValue(KAFKA_PRODUCER_BATCH_SIZE);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaProducerConfig.super.getBatchSize();
    }

    @Override
    public long getBufferMemory() {
        try {
            return configReader.getLongValue(KAFKA_PRODUCER_BUFFER_MEMORY);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaProducerConfig.super.getBufferMemory();
    }

    @Override
    public long getConnectionsMaxIdleInMs() {
        try {
            return configReader.getLongValue(KAFKA_PRODUCER_CONNECTION_MAX_IDLE_TIME_IN_MS);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaProducerConfig.super.getConnectionsMaxIdleInMs();
    }

    @Override
    public int getDeliveryTimeoutInMs() {
        try {
            return configReader.getIntValue(KAFKA_PRODUCER_DELIVERY_TIMEOUT_IN_MS);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaProducerConfig.super.getDeliveryTimeoutInMs();
    }

    @Override
    public boolean getEnableIdempotence() {
        try {
            return configReader.getBooleanValue(KAFKA_PRODUCER_ENABLE_IDEMPOTENCE);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaProducerConfig.super.getEnableIdempotence();
    }

    @Override
    public String getKeySerializer() {
        try {
            return configReader.getValue(KAFKA_PRODUCER_KEY_SERIALIZER);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaProducerConfig.super.getKeySerializer();
    }

    @Override
    public long getLingerInMs() {
        try {
            return configReader.getLongValue(KAFKA_PRODUCER_LINGER_IN_MS);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaProducerConfig.super.getLingerInMs();
    }

    @Override
    public long getMaxBlockInMs() {
        try {
            return configReader.getLongValue(KAFKA_PRODUCER_MAX_BLOCK_IN_MS);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaProducerConfig.super.getMaxBlockInMs();
    }

    @Override
    public int getMaxInFlightRequestsPerConnection() {
        try {
            return configReader.getIntValue(KAFKA_PRODUCER_MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaProducerConfig.super.getMaxInFlightRequestsPerConnection();
    }

    @Override
    public List<String> getProducerBootstrapServers() {
        try {
            return configReader.getTextArrayValue(KAFKA_PRODUCER_BOOTSTRAP_SERVERS);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return Collections.emptyList();
    }

    @Override
    public int getProducerRequestTimeoutInMs() {
        try {
            return configReader.getIntValue(KAFKA_PRODUCER_REQUEST_TIMEOUT_IN_MS);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaProducerConfig.super.getProducerRequestTimeoutInMs();
    }

    @Override
    public int getProducerSendBufferBytes() {
        try {
            return configReader.getIntValue(KAFKA_PRODUCER_SEND_BUFFER_BYTES);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaProducerConfig.super.getProducerSendBufferBytes();
    }

    @Override
    public int getProducerReceiveBufferBytes() {
        try {
            return configReader.getIntValue(KAFKA_PRODUCER_RECEIVE_BUFFER_BYTES);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaProducerConfig.super.getProducerReceiveBufferBytes();
    }

    @Override
    public int getRetries() {
        try {
            return configReader.getIntValue(KAFKA_PRODUCER_RETRIES);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaProducerConfig.super.getRetries();
    }

    @Override
    public String getValueSerializer() {
        try {
            return configReader.getValue(KAFKA_PRODUCER_VALUE_SERIALIZER);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaProducerConfig.super.getValueSerializer();
    }


    // Opensearch configs

    @Override
    public String getBulkPostRequestUrl() {
        try {
            return configReader.getValue(OPENSEARCH_BULK_POST_REQUEST_URL);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return OpensearchConfig.super.getBulkPostRequestUrl();
    }

    @Override
    public String getIndexName() {
        try {
            return configReader.getValue(OPENSEARCH_INDEX_NAME);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return OpensearchConfig.super.getIndexName();
    }

    @Override
    public String getPostRequestUrl() {
        try {
            return configReader.getValue(OPENSEARCH_POST_REQUEST_URL);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return OpensearchConfig.super.getPostRequestUrl();
    }

}
