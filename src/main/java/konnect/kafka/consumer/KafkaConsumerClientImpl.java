package konnect.kafka.consumer;

import com.google.common.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import konnect.config.AppConfig;
import konnect.opensearch.OpensearchRestClientImpl;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.MAX_POLL_RECORDS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;

public class KafkaConsumerClientImpl implements KafkaConsumerClient {

    private final AppConfig appConfig;
    private final Thread mainThread;
    private final OpensearchRestClientImpl opensearchRestClient;

    private AtomicBoolean closing;
    private final KafkaConsumer<String, String> kafkaConsumer;
    private Properties kafkaProps;


    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerClientImpl.class);

    public KafkaConsumerClientImpl(final AppConfig appConfig) {
        this.appConfig = appConfig;
        this.mainThread = Thread.currentThread();

        setProperties();
        this.kafkaConsumer = new KafkaConsumer<>(kafkaProps);
        this.closing = new AtomicBoolean(false);
        subscribeToTopic(appConfig.getKafkaTopicName());
        opensearchRestClient = new OpensearchRestClientImpl(appConfig);
    }

    private void setProperties() {
        kafkaProps = new Properties();
        kafkaProps.put(BOOTSTRAP_SERVERS_CONFIG, appConfig.getKafkaConsumerBootstrapServers());
        kafkaProps.put(KEY_DESERIALIZER_CLASS_CONFIG, appConfig.getKafkaConsumerKeyDeserializer());
        kafkaProps.put(VALUE_DESERIALIZER_CLASS_CONFIG, appConfig.getKafkaConsumerValueDeserializer());
        kafkaProps.put(AUTO_OFFSET_RESET_CONFIG, appConfig.getKafkaConsumerAutoOffsetReset());
        kafkaProps.put(MAX_POLL_RECORDS_CONFIG, appConfig.getKafkaConsumerMaxPollRecords());
        kafkaProps.put(GROUP_ID_CONFIG, appConfig.getKafkaConsumerGroupId());
    }

    private void subscribeToTopic(final String topic) {
        kafkaConsumer.subscribe(Collections.singletonList(topic));
    }

    @VisibleForTesting
    void setClosing() {
        LOGGER.info("Resetting the consumer loop");
        closing = new AtomicBoolean(true);
    }

    @VisibleForTesting
    void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Starting exit...");
            kafkaConsumer.wakeup();
            try {
                mainThread.join();
            } catch (final InterruptedException ex) {
                LOGGER.error("Error occurred, while trying to add shutdown hook", ex);
                Thread.currentThread().interrupt();
            }
        }));
    }

    public void processEvents() {
        addShutdownHook();

        Duration pollTimeout = Duration.ofMillis(appConfig.getKafkaConsumerPollIntervalInMs());

        try {
            while (!closing.get()) {
                processBulkEvents(pollTimeout);
            }
        } catch (final WakeupException ex) {
            LOGGER.info("Wake up exception!");
        } catch (final Exception ex) {
            LOGGER.error("Unexpected error", ex);
        } finally {
            kafkaConsumer.close();
            LOGGER.info("The consumer is now gracefully closed.");
        }

    }

    @VisibleForTesting
    void processBulkEvents(final Duration pollTimeout) {
        ConsumerRecords<String, String> records = kafkaConsumer.poll(pollTimeout);
        List<String> events = new ArrayList<>();

        for (final ConsumerRecord<String, String> event : records) {
            events.add(event.value());
        }
        kafkaConsumer.commitSync();

        if (!events.isEmpty()) {
            opensearchRestClient.publishBulkAsync(events);
        }
    }

}
