package konnect.common;

public class Constants {

    private Constants() {

    }

    // Kafka
    public static final String KAFKA_TOPIC_TOPIC_NAME = "kafka.topicName";

    // Kafka Consumer
    public static final String KAFKA_CONSUMER_ALLOW_AUTO_CREATE_TOPICS = "kafkaConsumer.allowAutoCreateTopics";
    public static final String KAFKA_CONSUMER_AUTO_COMMIT_INTERVAL_IN_MS = "kafkaConsumer.autoCommitIntervalInMs";
    public static final String KAFKA_CONSUMER_AUTO_OFFSET_RESET = "kafkaConsumer.autoOffsetReset";
    public static final String KAFKA_CONSUMER_BOOTSTRAP_SERVERS = "kafkaConsumer.bootstrapServers";
    public static final String KAFKA_CONSUMER_CONNECTION_MAX_IDLE_IN_MS = "kafkaConsumer.connectionMaxIdleInMs";
    public static final String KAFKA_CONSUMER_ENABLE_AUTO_COMMIT = "kafkaConsumer.enableAutoCommit";
    public static final String KAFKA_CONSUMER_GROUP_ID = "kafkaConsumer.groupId";
    public static final String KAFKA_CONSUMER_ISOLATION_LEVEL = "kafkaConsumer.isolationLevel";
    public static final String KAFKA_CONSUMER_KEY_DESERIALIZER = "kafkaConsumer.keyDeserializer";
    public static final String KAFKA_CONSUMER_MAX_POLL_RECORDS = "kafkaConsumer.maxPollRecords";
    public static final String KAFKA_CONSUMER_MAX_POLL_INTERVAL_IN_MS = "kafkaConsumer.maxPollIntervalInMs";
    public static final String KAFKA_CONSUMER_RECEIVER_BUFFER_BYTES = "kafkaConsumer.receiverBufferBytes";
    public static final String KAFKA_CONSUMER_REQUEST_TIMEOUT_IN_MS = "kafkaConsumer.requestTimeoutInMs";
    public static final String KAFKA_CONSUMER_SEND_BUFFER_BYTES = "kafkaConsumer.sendBufferBytes";
    public static final String KAFKA_CONSUMER_SESSION_TIMEOUT_IN_MS = "kafkaConsumer.sessionTimeoutInMs";
    public static final String KAFKA_CONSUMER_VALUE_DESERIALIZER = "kafkaConsumer.valueDeserializer";

    // Kafka Producer
    public static final String KAFKA_PRODUCER_ACKS = "kafkaProducer.acks";
    public static final String KAFKA_PRODUCER_BATCH_SIZE = "kafkaProducer.batchSize";
    public static final String KAFKA_PRODUCER_BOOTSTRAP_SERVERS = "kafkaProducer.bootstrapServers";
    public static final String KAFKA_PRODUCER_BUFFER_MEMORY = "kafkaProducer.bufferMemory";
    public static final String KAFKA_PRODUCER_CONNECTION_MAX_IDLE_TIME_IN_MS = "kafkaProducer.connectionsMaxIdleInMs";
    public static final String KAFKA_PRODUCER_DELIVERY_TIMEOUT_IN_MS = "kafkaProducer.deliveryTimeoutInMs";
    public static final String KAFKA_PRODUCER_ENABLE_IDEMPOTENCE = "kafkaProducer.enableIdempotence";
    public static final String KAFKA_PRODUCER_KEY_SERIALIZER = "kafkaProducer.keySerializer";
    public static final String KAFKA_PRODUCER_LINGER_IN_MS = "kafkaProducer.lingerInMs";
    public static final String KAFKA_PRODUCER_MAX_BLOCK_IN_MS = "kafkaProducer.maxBlockInMs";
    public static final String KAFKA_PRODUCER_MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION =
            "kafkaProducer.maxInFlightRequestsPerConnection";
    public static final String KAFKA_PRODUCER_RETRIES = "kafkaProducer.retries";
    public static final String KAFKA_PRODUCER_RECEIVE_BUFFER_BYTES = "kafkaProducer.receiveBufferBytes";
    public static final String KAFKA_PRODUCER_REQUEST_TIMEOUT_IN_MS = "kafkaProducer.requestTimeoutInMs";
    public static final String KAFKA_PRODUCER_SEND_BUFFER_BYTES = "kafkaProducer.sendBufferBytes";
    public static final String KAFKA_PRODUCER_VALUE_SERIALIZER = "kafkaProducer.valueSerializer";

    // httpClient
    public static final String HTTPCLIENT_CONNECT_TIMEOUT = "okHttpClient.connectTimeout";
    public static final String HTTPCLIENT_KEEP_ALIVE_DURATION = "okhttpClient.keepAliveDuration";
    public static final String HTTPCLIENT_MAX_IDLE_CONNECTIONS = "okhttpClient.maxIdleConnections";
    public static final String HTTPCLIENT_READ_TIMEOUT = "okhttpClient.readTimeout";
    public static final String HTTPCLIENT_WRITE_TIMEOUT = "okhttpClient.writeTimeout";

    // Opensearch
    public static final String OPENSEARCH_BULK_POST_REQUEST_URL = "opensearch.bulkPostRequestUrl";
    public static final String OPENSEARCH_INDEX_NAME = "opensearch.indexName";
    public static final String OPENSEARCH_POST_REQUEST_URL = "opensearch.postRequestUrl";

}