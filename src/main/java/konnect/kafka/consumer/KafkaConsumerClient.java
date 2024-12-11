package konnect.kafka.consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import konnect.config.AppConfig;
import konnect.opensearch.OpensearchPublisher;
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

public class KafkaConsumerClient {
    private final ExecutorService executorService;
    private final AppConfig appConfig;
    private final Thread mainThread;
    private final OpensearchPublisher opensearchPublisher;

    private KafkaConsumer<String, String> consumer;
    private Properties kafkaProps;

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerClient.class);

    public KafkaConsumerClient(final AppConfig appConfig) {
        this.appConfig = appConfig;
        this.mainThread = Thread.currentThread();

        setProperties();
        buildConsumer();
        subscribeToTopic(appConfig.getTopicName());
        opensearchPublisher = new OpensearchPublisher(appConfig);
        executorService = Executors.newFixedThreadPool(2); // need to add to config ???
        addShutdownHook();
    }

    private void setProperties() {
        kafkaProps = new Properties();
        kafkaProps.put(BOOTSTRAP_SERVERS_CONFIG, appConfig.getConsumerBootstrapServers());
        kafkaProps.put(KEY_DESERIALIZER_CLASS_CONFIG, appConfig.getKeyDeserializer());
        kafkaProps.put(VALUE_DESERIALIZER_CLASS_CONFIG, appConfig.getValueDeserializer());
        kafkaProps.put(AUTO_OFFSET_RESET_CONFIG, appConfig.getAutoOffsetReset());
        kafkaProps.put(MAX_POLL_RECORDS_CONFIG, appConfig.getMaxPollRecords());
        kafkaProps.put(GROUP_ID_CONFIG, appConfig.getGroupId());
    }

    private void buildConsumer() {
        consumer = new KafkaConsumer<>(kafkaProps);
    }

    private void subscribeToTopic(final String topic) {
        consumer.subscribe(Collections.singletonList(topic));
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Starting exit...");
            consumer.wakeup();
            try {
                mainThread.join();
            } catch (final InterruptedException ex) {
                LOGGER.error("Error occurred, while trying to add shutdown hook", ex);
                Thread.currentThread().interrupt();
            }
        }));
    }

    public void processRecords() {
        Duration timeout = Duration.ofMillis(100);

        executorService.submit(() -> {
            try {
                while (true) {
                    ConsumerRecords<String, String> records = consumer.poll(timeout);
                    List<String> events = new ArrayList<>();

                    for (final ConsumerRecord<String, String> event : records) {
                        events.add(event.value());
                    }
                    consumer.commitSync();

                    if (!events.isEmpty()) {
                        opensearchPublisher.publishBulk(events);
                    }
                }
            } catch (final WakeupException ex) {
                LOGGER.info("Wake up exception!");
            } catch (final Exception ex) {
                LOGGER.error("Unexpected error", ex);
            } finally {
                consumer.close();
                LOGGER.info("The consumer is now gracefully closed.");
            }
        });
    }

}
