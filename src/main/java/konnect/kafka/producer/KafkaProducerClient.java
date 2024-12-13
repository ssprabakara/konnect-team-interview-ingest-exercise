package konnect.kafka.producer;

public interface KafkaProducerClient {

    void sendDataSync(final Object value);
    void sendDataAsync(final Object value);
    void sendData(final Object value);

}
