package konnect.kafka.config;

import java.util.List;

public interface KafkaConsumerConfig {
    String AUTO_OFFSET_RESET = "earliest";
    String DEFAULT_ISOLATION_LEVEL = "read_uncommitted";
    String STRING_DESERIALIZER = "org.apache.kafka.common.serialization.StringDeserializer";

    /**
     * Property: `allow.auto.crete.topics`
     *
     * Allow automatic topic creation on the broker when subscribing to or assigning a topic. A topic being subscribed
     * to will be automatically created only if the broker allows for it using `auto.create.topics.enable` broker
     * configuration. This configuration must be set to `false` when using brokers older than 0.11.0
     *
     * Importance: Medium
     *
     * @return the boolean value for allowing the consumer to auto create the topic
     */
    boolean getKafkaConsumerAllowAutoCreateTopics();

    /**
     * Property: `receive.buffer.bytes`
     *
     * The size of the TCP receive buffer (SO_RCVBUF) to use when reading data. If the value is -1, the OS default
     * will be used.
     *
     * Importance: Medium
     *
     * @return the interger value for auto commit interval in milliseconds
     */
    default int getKafkaConsumerAutoCommitIntervalInMs() {
        return 5000; // 5 seconds
    }

    /**
     * Property: `auto.offset.reset`
     *
     * What to do when there is no initial offset in Kafka or if the current offset does not exist any more on the
     * server (e.g. because that data has been deleted):
     *
     * earliest: automatically reset the offset to the earliest offset
     * latest: automatically reset the offset to the latest offset
     * none: throw exception to the consumer if no previous offset is found for the consumer's group
     * anything else: throw exception to the consumer.
     *
     * Note that altering partition numbers while setting this config to latest may cause message delivery loss since
     * producers could start to send messages to newly added partitions (i.e. no initial offsets exist yet) before
     * consumers reset their offsets.
     *
     * Importance: Medium
     *
     * @return the default string value for the consumer auto offset reset behavior
     */
    default String getKafkaConsumerAutoOffsetReset() {
        return AUTO_OFFSET_RESET;
    }

    /**
     * Property: `connenction.max.idle.ms`
     *
     * Close idle connections after the number of milliseconds specified by this config.
     *
     * Importance: Medium
     *
     * @return the long value for the maximum number of millliseconds to wait before closing the idle connections
     */
    default long getKafkaConsumerConnectionMaxIdleInMs() {
        return 540000; // 9 minutes
    }

    /**
     * Property: `bootstrap.servers`
     *
     * A list of host/port pairs used to establish the initial connection to the Kafka cluster. Clients use this list
     * to bootstrap and discover the full set of Kafka brokers. While the order of servers in the list does not matter,
     * we recommend including more than one server to ensure resilience if any servers are down. This list does not need
     * to contain the entire set of brokers, as Kafka clients automatically manage and update connections to the cluster
     * efficiently. This list must be in the form host1:port1,host2:port2,....
     *
     * Importance: High
     *
     * @return  the List of Strings of the bootstrap servers
     */
    List<String> getKafkaConsumerBootstrapServers();

    /**
     * Property: `receive.buffer.bytes`
     *
     * The size of the TCP receive buffer (SO_RCVBUF) to use when reading data. If the value is -1, the OS default
     * will be used.
     *
     * Importance: Medium
     *
     * @return int
     */
    default int getKafkaConsumerReceiveBufferBytes() {
        return 65536; // 64KB
    }

    /**
     * Property: `request.timeout.ms`
     *
     * The configuration controls the maximum amount of time the client will wait for the response of a request. If the
     * response is not received before the timeout elapses the client will resend the request if necessary or fail the
     * request if retries are exhausted. This should be larger than replica.lag.time.max.ms (a broker configuration)
     * to reduce the possibility of message duplication due to unnecessary producer retries.
     *
     * Importance: Medium
     *
     * @return int
     */
    default int getKafkaConsumerRequestTimeoutInMs() {
        return 30000; // 30 seconds
    }

    /**
     * Property: `send.buffer.bytes`
     *
     * The size of the TCP send buffer (SO_SNDBUF) to use when sending data. If the value is -1, the OS default
     * will be used.
     *
     * Importance: Medium
     *
     * @return int
     */
    default int getKafkaConsumerSendBufferBytes() {
        return 131072; // 128KB
    }

    /**
     * Property: `enable.auto.commit`
     *
     * If true the consumer's offset will be periodically committed in the background.
     *
     * Importance: Medium
     *
     * @return boolean
     */
    default boolean getKafkaConsumerEnableAutoCommit() {
        return true;
    }

    /**
     * Property: `group.id`
     *
     * A unique string that identifies the consumer group this consumer belongs to. This property is required if the
     * consumer uses either the group management functionality by using `subscribe(topic)` or the Kafka-based offset
     * management strategy.
     *
     * Importance: High
     *
     * @return String
     */
    String getKafkaConsumerGroupId();

    /**
     * Property: `isolation.level`
     *
     * Controls how to read messages written transactionally. If set to `read_committed`, consumer.poll() will only
     * return transactional messages which have been committed. If set to `read_uncommitted` (the default),
     * consumer.poll() will return all messages, even transactional messages which have been aborted. Non-transactional
     * messages will be returned unconditionally in either mode.
     *
     * Messages will always be returned in offset order. Hence, in `read_committed` mode, consumer.poll() will only
     * return messages up to the last stable offset (LSO), which is the one less than the offset of the first open
     * transaction. In particular any messages appearing after messages belonging to ongoing transactions will be
     * withheld until the relevant transaction has been completed. As a result, `read_committed `consumers will not be
     * able to read up to the high watermark when there are in flight transactions.
     *
     * Further, when in `read_committed` the seekToEnd method will return the LSO
     *
     * Importance: Medium
     *
     * @return String
     */
    default String getKafkaConsumerIsolationLevel() {
        return DEFAULT_ISOLATION_LEVEL;
    }

    /**
     * Property: `key.deserializer`
     *
     * Serializer class for key that implements the `org.apache.kafka.common.serialization.Deserializer` interface.
     *
     * Importance: High
     *
     * @return String
     */
    default String getKafkaConsumerKeyDeserializer() {
        return STRING_DESERIALIZER;
    }

    /**
     *  Property: `max.poll.interval.ms`
     *
     * The maximum delay between invocations of poll() when using consumer group management. This places an upper bound
     * on the amount of time that the consumer can be idle before fetching more records. If poll() is not called before
     * expiration of this timeout, then the consumer is considered failed and the group will re-balance in order to
     * reassign the partitions to another member. For consumers using a non-null group.instance.id which reach this
     * timeout, partitions will not be immediately reassigned. Instead, the consumer will stop sending heartbeats and
     * partitions will be reassigned after expiration of `session.timeout.ms`. This mirrors the behavior of a static
     * consumer which has shutdown.
     *
     * Importance: Medium
     *
     * @return the default integer value of max interval for polling in ms
     */
    default int getKafkaConsumerMaxPollIntervalInMs() {
        return 300000; // 5 minutes
    }

    /**
     * Property: `max.poll.records`
     *
     * The maximum number of records returned in a single call to poll(). Note, that max.poll.records does not impact
     * the underlying fetching behavior. The consumer will cache the records from each fetch request and returns them
     * incrementally from each poll.
     *
     * Importance: Medium
     *
     * @return int
     */
    default int getKafkaConsumerMaxPollRecords() {
        return 500;
    }

    /**
     * The delay between invocations of poll() when using consumer group management.
     *
     * @return int
     */
    default int getKafkaConsumerPollIntervalInMs() {
        return 100;
    }

    /**
     * Property: `session.timeout.ms`
     *
     * The timeout used to detect client failures when using Kafka's group management facility. The client sends
     * periodic heartbeats to indicate its liveness to the broker. If no heartbeats are received by the broker before
     * the expiration of this session timeout, then the broker will remove this client from the group and initiate
     * a re-balance. Note that the value must be in the allowable range as configured in the broker configuration by
     * `group.min.session.timeout.ms` and `group.max.session.timeout.ms`
     *
     * Importance: High
     *
     * @return int
     */
    default int getKafkaConsumerSessionTimeoutInMs() {
        return 45000; // 45 seconds
    }

    /**
     * Property: `value.deserializer`
     *
     * Serializer class for value that implements the `org.apache.kafka.common.serialization.Deserializer` interface.
     *
     * Importance: High
     *
     * @return String
     */
    default String getKafkaConsumerValueDeserializer() {
        return STRING_DESERIALIZER;
    }

}
