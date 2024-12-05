package konnect;

import com.fasterxml.jackson.databind.JsonNode;
import konnect.kafka.consumer.KConsumer;
import konnect.kafka.producer.KProducer;
import konnect.util.Utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Driver {
    public static final String INPUT_FILE_PATH = "./input/stream.jsonl";
    public static void main(final String[] args) {
        KConsumer kConsumer = new KConsumer();
        kConsumer.processRecords();

        KProducer kProducer = new KProducer();
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
