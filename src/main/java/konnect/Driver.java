package konnect;

import com.fasterxml.jackson.databind.JsonNode;
import konnect.config.ConfigReader;
import konnect.config.AppConfig;
import konnect.kafka.consumer.KafkaConsumerClient;
import konnect.kafka.producer.KafkaProducerClient;
import konnect.util.Utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Driver {
    public static final String INPUT_FILE_PATH = "./input/stream.jsonl";

    public static void main(final String[] args) {
        AppConfig kafkaConfig = new AppConfig(new ConfigReader());
        KafkaConsumerClient kConsumer = new KafkaConsumerClient(kafkaConfig);
        kConsumer.processRecords();

        KafkaProducerClient kProducer = new KafkaProducerClient(kafkaConfig);
        try (
            FileReader reader = new FileReader(INPUT_FILE_PATH);
            BufferedReader bufferedReader = new BufferedReader(reader);
        ) {
            String currentLine;
            while ((currentLine = bufferedReader.readLine()) != null) {
                JsonNode jsonNode = Utils.OBJECT_MAPPER.readTree(currentLine);
                kProducer.sendDataASync(jsonNode);
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }
}
