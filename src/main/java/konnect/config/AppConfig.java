package konnect.config;

import java.util.Collections;
import java.util.List;
import konnect.exception.ConfigNotFoundException;
import konnect.kafka.config.KafkaConsumerConfig;
import konnect.kafka.config.KafkaProducerConfig;
import konnect.kafka.config.KafkaTopicConfig;
import konnect.opensearch.config.OpensearchConfig;
import konnect.opensearch.config.OpensearchRestClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import static konnect.common.Constants.KAFKA_CONSUMER_POLL_INTERVAL_IN_MS;
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
import static konnect.common.Constants.OPENSEARCH_REST_CLIENT_CONNECT_TIMEOUT;
import static konnect.common.Constants.OPENSEARCH_REST_CLIENT_HOST;
import static konnect.common.Constants.OPENSEARCH_REST_CLIENT_HTTP_SCHEME;
import static konnect.common.Constants.OPENSEARCH_REST_CLIENT_MAX_RETRY;
import static konnect.common.Constants.OPENSEARCH_REST_CLIENT_PORT;

public class AppConfig implements KafkaConsumerConfig,
                                  KafkaProducerConfig,
                                  KafkaTopicConfig,
                                  OpensearchConfig,
                                  OpensearchRestClientConfig {

    private final ConfigReader configReader;
    private static final Logger LOGGER = LoggerFactory.getLogger(AppConfig.class);

    public AppConfig(final ConfigReader configReader) {
        this.configReader = configReader;
    }

    // Kafka Topic configs

    @Override
    public String getKafkaTopicName() {
        try {
            return configReader.getValue(KAFKA_TOPIC_TOPIC_NAME);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaTopicConfig.super.getKafkaTopicName();
    }


    // Kafka Consumer configs

    @Override
    public boolean getKafkaConsumerAllowAutoCreateTopics() {
        try {
            return configReader.getBooleanValue(KAFKA_CONSUMER_ALLOW_AUTO_CREATE_TOPICS);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return false;
    }

    @Override
    public int getKafkaConsumerAutoCommitIntervalInMs() {
        try {
            return configReader.getIntValue(KAFKA_CONSUMER_AUTO_COMMIT_INTERVAL_IN_MS);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaConsumerConfig.super.getKafkaConsumerAutoCommitIntervalInMs();
    }

    @Override
    public String getKafkaConsumerAutoOffsetReset() {
        try {
            return configReader.getValue(KAFKA_CONSUMER_AUTO_OFFSET_RESET);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaConsumerConfig.super.getKafkaConsumerAutoOffsetReset();
    }

    @Override
    public long getKafkaConsumerConnectionMaxIdleInMs() {
        try {
            return configReader.getLongValue(KAFKA_CONSUMER_CONNECTION_MAX_IDLE_IN_MS);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaConsumerConfig.super.getKafkaConsumerConnectionMaxIdleInMs();
    }

    @Override
    public List<String> getKafkaConsumerBootstrapServers() {
        try {
            return configReader.getTextArrayValue(KAFKA_CONSUMER_BOOTSTRAP_SERVERS);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return Collections.emptyList();
    }

    @Override
    public int getKafkaConsumerReceiveBufferBytes() {
        try {
            return configReader.getIntValue(KAFKA_CONSUMER_RECEIVER_BUFFER_BYTES);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaConsumerConfig.super.getKafkaConsumerReceiveBufferBytes();
    }

    @Override
    public int getKafkaConsumerRequestTimeoutInMs() {
        try {
            return configReader.getIntValue(KAFKA_CONSUMER_REQUEST_TIMEOUT_IN_MS);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaConsumerConfig.super.getKafkaConsumerRequestTimeoutInMs();
    }

    @Override
    public int getKafkaConsumerSendBufferBytes() {
        try {
            return configReader.getIntValue(KAFKA_CONSUMER_SEND_BUFFER_BYTES);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaConsumerConfig.super.getKafkaConsumerSendBufferBytes();
    }

    @Override
    public boolean getKafkaConsumerEnableAutoCommit() {
        try {
            return configReader.getBooleanValue(KAFKA_CONSUMER_ENABLE_AUTO_COMMIT);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaConsumerConfig.super.getKafkaConsumerEnableAutoCommit();
    }

    @Override
    public String getKafkaConsumerGroupId() {
        try {
            return configReader.getValue(KAFKA_CONSUMER_GROUP_ID);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return null;
    }

    @Override
    public String getKafkaConsumerIsolationLevel() {
        try {
            return configReader.getValue(KAFKA_CONSUMER_ISOLATION_LEVEL);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaConsumerConfig.super.getKafkaConsumerIsolationLevel();
    }

    @Override
    public String getKafkaConsumerKeyDeserializer() {
        try {
            return configReader.getValue(KAFKA_CONSUMER_KEY_DESERIALIZER);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaConsumerConfig.super.getKafkaConsumerKeyDeserializer();
    }

    @Override
    public int getKafkaConsumerMaxPollIntervalInMs() {
        try {
            return configReader.getIntValue(KAFKA_CONSUMER_MAX_POLL_INTERVAL_IN_MS);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaConsumerConfig.super.getKafkaConsumerMaxPollIntervalInMs();
    }

    @Override
    public int getKafkaConsumerMaxPollRecords() {
        try {
            return configReader.getIntValue(KAFKA_CONSUMER_MAX_POLL_RECORDS);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaConsumerConfig.super.getKafkaConsumerMaxPollRecords();
    }

    @Override
    public int getKafkaConsumerPollIntervalInMs() {
        try {
            return configReader.getIntValue(KAFKA_CONSUMER_POLL_INTERVAL_IN_MS);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaConsumerConfig.super.getKafkaConsumerPollIntervalInMs();
    }

    @Override
    public int getKafkaConsumerSessionTimeoutInMs() {
        try {
            return configReader.getIntValue(KAFKA_CONSUMER_SESSION_TIMEOUT_IN_MS);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaConsumerConfig.super.getKafkaConsumerSessionTimeoutInMs();
    }

    @Override
    public String getKafkaConsumerValueDeserializer() {
        try {
            return configReader.getValue(KAFKA_CONSUMER_VALUE_DESERIALIZER);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaConsumerConfig.super.getKafkaConsumerValueDeserializer();
    }


    // Kafka Producer Configs

    @Override
    public String getKafkaProducerAcks() {
        try {
            return configReader.getValue(KAFKA_PRODUCER_ACKS);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaProducerConfig.super.getKafkaProducerAcks();
    }

    @Override
    public int getKafkaProducerBatchSize() {
        try {
            return configReader.getIntValue(KAFKA_PRODUCER_BATCH_SIZE);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaProducerConfig.super.getKafkaProducerBatchSize();
    }

    @Override
    public long getKafkaProducerBufferMemory() {
        try {
            return configReader.getLongValue(KAFKA_PRODUCER_BUFFER_MEMORY);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaProducerConfig.super.getKafkaProducerBufferMemory();
    }

    @Override
    public long getKafkaProducerConnectionsMaxIdleInMs() {
        try {
            return configReader.getLongValue(KAFKA_PRODUCER_CONNECTION_MAX_IDLE_TIME_IN_MS);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaProducerConfig.super.getKafkaProducerConnectionsMaxIdleInMs();
    }

    @Override
    public int getKafkaProducerDeliveryTimeoutInMs() {
        try {
            return configReader.getIntValue(KAFKA_PRODUCER_DELIVERY_TIMEOUT_IN_MS);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaProducerConfig.super.getKafkaProducerDeliveryTimeoutInMs();
    }

    @Override
    public boolean getKafkaProducerEnableIdempotence() {
        try {
            return configReader.getBooleanValue(KAFKA_PRODUCER_ENABLE_IDEMPOTENCE);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaProducerConfig.super.getKafkaProducerEnableIdempotence();
    }

    @Override
    public String getKafkaProducerKeySerializer() {
        try {
            return configReader.getValue(KAFKA_PRODUCER_KEY_SERIALIZER);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaProducerConfig.super.getKafkaProducerKeySerializer();
    }

    @Override
    public long getKafkaProducerLingerInMs() {
        try {
            return configReader.getLongValue(KAFKA_PRODUCER_LINGER_IN_MS);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaProducerConfig.super.getKafkaProducerLingerInMs();
    }

    @Override
    public long getKafkaProducerMaxBlockInMs() {
        try {
            return configReader.getLongValue(KAFKA_PRODUCER_MAX_BLOCK_IN_MS);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaProducerConfig.super.getKafkaProducerMaxBlockInMs();
    }

    @Override
    public int getKafkaProducerMaxInFlightRequestsPerConnection() {
        try {
            return configReader.getIntValue(KAFKA_PRODUCER_MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaProducerConfig.super.getKafkaProducerMaxInFlightRequestsPerConnection();
    }

    @Override
    public List<String> getKafkaProducerBootstrapServers() {
        try {
            return configReader.getTextArrayValue(KAFKA_PRODUCER_BOOTSTRAP_SERVERS);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return Collections.emptyList();
    }

    @Override
    public int getKafkaProducerRequestTimeoutInMs() {
        try {
            return configReader.getIntValue(KAFKA_PRODUCER_REQUEST_TIMEOUT_IN_MS);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaProducerConfig.super.getKafkaProducerRequestTimeoutInMs();
    }

    @Override
    public int getKafkaProducerSendBufferBytes() {
        try {
            return configReader.getIntValue(KAFKA_PRODUCER_SEND_BUFFER_BYTES);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaProducerConfig.super.getKafkaProducerSendBufferBytes();
    }

    @Override
    public int getKafkaProducerReceiveBufferBytes() {
        try {
            return configReader.getIntValue(KAFKA_PRODUCER_RECEIVE_BUFFER_BYTES);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaProducerConfig.super.getKafkaProducerReceiveBufferBytes();
    }

    @Override
    public int getKafkaProducerRetries() {
        try {
            return configReader.getIntValue(KAFKA_PRODUCER_RETRIES);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaProducerConfig.super.getKafkaProducerRetries();
    }

    @Override
    public String getKafkaProducerValueSerializer() {
        try {
            return configReader.getValue(KAFKA_PRODUCER_VALUE_SERIALIZER);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return KafkaProducerConfig.super.getKafkaProducerValueSerializer();
    }


    // Opensearch configs

    @Override
    public String getOpensearchBulkPostRequestUrl() {
        try {
            return configReader.getValue(OPENSEARCH_BULK_POST_REQUEST_URL);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return OpensearchConfig.super.getOpensearchBulkPostRequestUrl();
    }

    @Override
    public String getOpensearchIndexName() {
        try {
            return configReader.getValue(OPENSEARCH_INDEX_NAME);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return OpensearchConfig.super.getOpensearchIndexName();
    }

    @Override
    public String getOpensearchPostRequestUrl() {
        try {
            return configReader.getValue(OPENSEARCH_POST_REQUEST_URL);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return OpensearchConfig.super.getOpensearchPostRequestUrl();
    }


    // Opensearch REST Client configs

    @Override
    public int getOpensearchRestClientConnectTimeoutInSeconds() {
        try {
            return configReader.getIntValue(OPENSEARCH_REST_CLIENT_CONNECT_TIMEOUT);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return OpensearchRestClientConfig.super.getOpensearchRestClientConnectTimeoutInSeconds();
    }

    @Override
    public String getOpensearchRestClientHost() {
        try {
            return configReader.getValue(OPENSEARCH_REST_CLIENT_HOST);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return OpensearchRestClientConfig.super.getOpensearchRestClientHost();
    }

    @Override
    public int getOpensearchRestClientPort() {
        try {
            return configReader.getIntValue(OPENSEARCH_REST_CLIENT_PORT);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return OpensearchRestClientConfig.super.getOpensearchRestClientPort();
    }

    @Override
    public String getOpensearchRestClientHttpScheme() {
        try {
            return configReader.getValue(OPENSEARCH_REST_CLIENT_HTTP_SCHEME);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return OpensearchRestClientConfig.super.getOpensearchRestClientHttpScheme();
    }

    @Override
    public int getOpenSearchRestClientMaxRetry() {
        try {
            return configReader.getIntValue(OPENSEARCH_REST_CLIENT_MAX_RETRY);
        } catch (final ConfigNotFoundException cnfe) {
            LOGGER.error(cnfe.getMessage());
        }
        return OpensearchRestClientConfig.super.getOpenSearchRestClientMaxRetry();
    }

}
