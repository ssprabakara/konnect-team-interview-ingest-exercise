package konnect;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.concurrent.Executors;
import konnect.config.ConfigReader;
import konnect.config.AppConfig;
import konnect.kafka.consumer.KafkaConsumerClientImpl;
import konnect.kafka.producer.KafkaProducerClientImpl;
import konnect.util.Utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


/**
 * This is the main driver code to test the exercise
 * It starts the Kafka consumer and kafka producer and will
 * read the jsonl file and publishes the events to the kafka
 * and the consumer will consume those events and publish it
 * to opensearch.
 *
 * The consumer will try to run forever, until the java
 * process is killed or terminated.
 *
 */
public class Driver {
    public static final String INPUT_FILE_PATH = "./input/stream.jsonl";

    public static void main(final String[] args) {
        AppConfig kafkaConfig = new AppConfig(new ConfigReader());
        KafkaConsumerClientImpl kConsumer = new KafkaConsumerClientImpl(kafkaConfig);
        Executors.newFixedThreadPool(1).submit(kConsumer::processEvents);

        KafkaProducerClientImpl kProducer = new KafkaProducerClientImpl(kafkaConfig);
        try (
            FileReader reader = new FileReader(INPUT_FILE_PATH);
            BufferedReader bufferedReader = new BufferedReader(reader);
        ) {
            String currentLine;
            while ((currentLine = bufferedReader.readLine()) != null) {
                JsonNode jsonNode = Utils.OBJECT_MAPPER.readTree(currentLine);
                kProducer.sendDataAsync(jsonNode);
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }
}
