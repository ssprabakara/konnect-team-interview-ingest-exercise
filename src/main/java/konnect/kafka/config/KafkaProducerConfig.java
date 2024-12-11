package konnect.kafka.config;

import java.util.List;

public interface KafkaProducerConfig {
    String DEFAULT_ACK = "all";
    String STRING_SERIALIZER = "org.apache.kafka.common.serialization.StringSerializer";

    /**
     * Property: `acks`
     *
     * The number of acknowledgments the producer requires the leader to have received before considering a request
     * complete. This controls the durability of records that are sent. The following settings are allowed:
     *
     * acks=0 If set to zero then the producer will not wait for any acknowledgment from the server at all. The record
     * will be immediately added to the socket buffer and considered sent. No guarantee can be made that the server has
     * received the record in this case, and the retries configuration will not take effect (as the client won't
     * generally know of any failures). The offset given back for each record will always be set to -1.
     *
     * acks=1 This will mean the leader will write the record to its local log but will respond without awaiting full
     * acknowledgment from all followers. In this case should the leader fail immediately after acknowledging the
     * record but before the followers have replicated it then the record will be lost.
     *
     * acks=all This means the leader will wait for the full set of in-sync replicas to acknowledge the record. This
     * guarantees that the record will not be lost as long as at least one in-sync replica remains alive. This is the
     * strongest available guarantee. This is equivalent to the acks=-1 setting.
     *
     * Note that enabling idempotence requires this config value to be 'all'. If conflicting configurations are set and
     * idempotence is not explicitly enabled, idempotence is disabled.
     *
     * @return String
     *
     * Importance: Low
     **/
    default String getAcks() {
        return DEFAULT_ACK;
    }

    /**
     * Property: `batch.size`
     *
     * The producer will attempt to batch records together into fewer requests whenever multiple records are being sent
     * to the same partition. This helps performance on both the client and the server. This configuration controls the
     * default batch size in bytes.
     *
     * No attempt will be made to batch records larger than this size.
     *
     * Requests sent to brokers will contain multiple batches, one for each partition with data available to be sent.
     *
     * A small batch size will make batching less common and may reduce throughput (a batch size of zero will disable
     * batching entirely). A very large batch size may use memory a bit more wastefully as we will always allocate a
     * buffer of the specified batch size in anticipation of additional records.
     *
     * Note: This setting gives the upper bound of the batch size to be sent. If we have fewer than this many bytes
     * accumulated for this partition, we will 'linger' for the linger.ms time waiting for more records to show up.
     * This `linger.ms` setting defaults to 0, which means we'll immediately send out a record even the accumulated
     * batch size is under this `batch.size` setting.
     *
     * @return int
     *
     * Importance: Medium
     */
    default int getBatchSize() {
        return 16384;
    }

    /**
     * Property: `buffer.memory`
     *
     * The total bytes of memory the producer can use to buffer records waiting to be sent to the server. If records
     * are sent faster than they can be delivered to the server the producer will block for max.block.ms after which
     * it will throw an exception.
     *
     * This setting should correspond roughly to the total memory the producer will use, but is not a hard bound since
     * not all memory the producer uses is used for buffering. Some additional memory will be used for compression
     * (if compression is enabled) as well as for maintaining in-flight requests.
     *
     * @return long
     *
     * Importance: High
     */
    default long getBufferMemory() {
        return 33554432L;
    }

    /**
     * Property: `connection.max.idle.ms`
     *
     * Close idle connections after the number of milliseconds specified by this config.
     *
     * @return long
     *
     * Importance: Medium
     */
    default long getConnectionsMaxIdleInMs() {
        return 540000L; // 9 minutes
    }

    /**
     * Property: `delivery.timeout.ms`
     *
     * An upper bound on the time to report success or failure after a call to send() returns. This limits the total
     * time that a record will be delayed prior to sending, the time to await acknowledgement from the broker
     * (if expected), and the time allowed for retriable send failures. The producer may report failure to send a
     * record earlier than this config if either an unrecoverable error is encountered, the retries have been exhausted,
     * or the record is added to a batch which reached an earlier delivery expiration deadline. The value of this config
     * should be greater than or equal to the sum of `request.timeout.ms` and `linger.ms`.
     *
     * @return int
     *
     * Importance: Medium
     */
    default int getDeliveryTimeoutInMs() {
        return 120000; // 2 minutes
    }

    /**
     * Property: `enable.idempotence`
     *
     * When set to 'true', the producer will ensure that exactly one copy of each message is written in the stream.
     * If 'false', producer retries due to broker failures, etc., may write duplicates of the retried message in the
     * stream. Note that enabling idempotence requires max.in.flight.requests.per.connection to be less than or equal
     * to 5 (with message ordering preserved for any allowable value), retries to be greater than 0, and acks must
     * be 'all'.
     *
     * Idempotence is enabled by default if no conflicting configurations are set. If conflicting configurations are
     * set and idempotence is not explicitly enabled, idempotence is disabled. If idempotence is explicitly enabled
     * and conflicting configurations are set, a ConfigException is thrown.
     *
     * @return boolean
     *
     * Importance: Low
     **/
    default boolean getEnableIdempotence() {
        return true;
    }

    /**
     * Property: `key.serializer`
     *
     * Serializer class for key that implements the `org.apache.kafka.common.serialization.Serializer` interface.
     *
     * @return String
     *
     * Importance: High
     */
    default String getKeySerializer() {
        return STRING_SERIALIZER;
    }

    /**
     * Property: `linger.ms`
     *
     * The producer groups together any records that arrive in between request transmissions into a single batched
     * request. Normally this occurs only under load when records arrive faster than they can be sent out. However, in
     * some circumstances the client may want to reduce the number of requests even under moderate load. This setting
     * accomplishes this by adding a small amount of artificial delay—that is, rather than immediately sending out a
     * record, the producer will wait for up to the given delay to allow other records to be sent so that the sends can
     * be batched together. This can be thought of as analogous to Nagle's algorithm in TCP. This setting gives the
     * upper bound on the delay for batching: once we get `batch.size` worth of records for a partition it will be sent
     * immediately regardless of this setting, however if we have fewer than this many bytes accumulated for this
     * partition we will 'linger' for the specified time waiting for more records to show up. This setting defaults to
     * 0 (i.e. no delay). Setting `linger.ms=5`, for example, would have the effect of reducing the number of requests
     * sent but would add up to 5ms of latency to records sent in the absence of load.
     *
     * @return long
     *
     * Importance: Medium
     */
    default long getLingerInMs() {
        return 0L;
    }

    /**
     * Property: `max.block.ms`
     *
     * The configuration controls how long the KafkaProducer's `send()`, `partitionsFor()`, `initTransactions()`,
     * `sendOffsetsToTransaction()`, `commitTransaction()` and `abortTransaction()` methods will block. For `send()`
     * this timeout bounds the total time waiting for both metadata fetch and buffer allocation (blocking in the
     * user-supplied serializers or partitioner is not counted against this timeout). For `partitionsFor()` this timeout
     * bounds the time spent waiting for metadata if it is unavailable. The transaction-related methods always block,
     * but may timeout if the transaction coordinator could not be discovered or did not respond within the timeout.
     *
     * @return long
     *
     * Importance: Medium
     */
    default long getMaxBlockInMs() {
        return 60000L; // 1 minute
    }

    /**
     * Property: `max.in.flight.requests.per.connection`
     *
     * The maximum number of unacknowledged requests the client will send on a single connection before blocking. Note
     * that if this configuration is set to be greater than 1 and enable.idempotence is set to false, there is a risk
     * of message reordering after a failed send due to retries (i.e., if retries are enabled); if retries are disabled
     * or if enable.idempotence is set to true, ordering will be preserved. Additionally, enabling idempotence requires
     * the value of this configuration to be less than or equal to 5. If conflicting configurations are set and
     * idempotence is not explicitly enabled, idempotence is disabled.
     *
     * @return int
     *
     * Importance: Low
     **/
    default int getMaxInFlightRequestsPerConnection() {
        return 5;
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
     * @return `List<String>`
     *
     * Importance: High
     */
    List<String> getProducerBootstrapServers();

    /**
     * Property: `request.timeout.ms`
     *
     * The configuration controls the maximum amount of time the client will wait for the response of a request. If the
     * response is not received before the timeout elapses the client will resend the request if necessary or fail the
     * request if retries are exhausted. This should be larger than replica.lag.time.max.ms (a broker configuration)
     * to reduce the possibility of message duplication due to unnecessary producer retries.
     *
     * @return int
     *
     * Importance: Medium
     */
    default int getProducerRequestTimeoutInMs() {
        return 30000; // 30 seconds
    }

    /**
     * Property: `send.buffer.bytes`
     *
     * The size of the TCP send buffer (SO_SNDBUF) to use when sending data. If the value is -1, the OS default
     * will be used.
     *
     * @return int
     *
     * Importance: Medium
     */
    default int getProducerSendBufferBytes() {
        return 131072; // 128KB
    }

    /**
     * Property: `receive.buffer.bytes`
     *
     * The size of the TCP receive buffer (SO_RCVBUF) to use when reading data. If the value is -1, the OS default
     * will be used.
     *
     * @return int
     *
     * Importance: Medium
     */
    default int getProducerReceiveBufferBytes() {
        return 32768; // 32KB
    }

    /**
     * Property: `retries`
     *
     * Setting a value greater than zero will cause the client to resend any record whose send fails with a potentially
     * transient error. Note that this retry is no different, if the client resent the record upon receiving the
     * error. Produce requests will be failed before the number of retries has been exhausted if the timeout configured
     * by delivery.timeout.ms expires first before successful acknowledgement. Users should generally prefer to leave
     * this config unset and instead use delivery.timeout.ms to control retry behavior.
     *
     * Enabling idempotence requires this config value to be greater than 0. If conflicting configurations are set and
     * idempotence is not explicitly enabled, idempotence is disabled.
     *
     * Allowing retries while setting enable.idempotence to false and max.in.flight.requests.per.connection to greater
     * than 1 will potentially change the ordering of records because if two batches are sent to a single partition,
     * and the first fails and is retried but the second succeeds, then the records in the second batch may appear first.
     *
     * Importance: High
     *
     * @return int
     */
    default int getRetries() {
        return 2147483647;
    }

    /**
     * Property: `value.serializer`
     *
     * Serializer class for value that implements the `org.apache.kafka.common.serialization.Serializer` interface.
     *
     * @return String
     *
     * Importance: High
     */
    default String getValueSerializer() {
        return STRING_SERIALIZER;
    }

}
