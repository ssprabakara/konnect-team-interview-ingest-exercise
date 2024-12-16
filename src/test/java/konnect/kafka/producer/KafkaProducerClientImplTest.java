package konnect.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.concurrent.Future;
import konnect.config.AppConfig;
import konnect.config.ConfigReader;
import konnect.util.Utils;
import nl.altindag.log.LogCaptor;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class KafkaProducerClientImplTest {

    private KafkaProducerClientImpl kafkaProducerClient;
    private KafkaProducerClientImpl kafkaProducerClientSpy;
    private KafkaProducer<String, Object> kafkaProducer;

    private static final String CDC_EVENT = "{\"before\": null, \"after\": " +
            "{\"key\": \"c/04397908-e846-4019-aeaa-2422a1cb7b6c/o/store_event/last_update\", " +
            "\"value\": {\"type\": 3, \"object\": {\"id\": \"c/04397908-e846-4019-aeaa-2422a1cb7b6c/last_update\", " +
            "\"value\": \"9c341d3d-1a20-4d72-b95c-3dddb08c6602\", \"resource_type\": \"key\"}}}, " +
            "\"op\": \"u\", \"ts_ms\": 1706812604828}";

    @Before
    public void mockSetup() {
        AppConfig appConfig = new AppConfig(new ConfigReader());
        kafkaProducer = mock(KafkaProducer.class);
        kafkaProducerClient = new KafkaProducerClientImpl(appConfig);
        ReflectionTestUtils.setField(kafkaProducerClient, "kafkaProducer", kafkaProducer);
        kafkaProducerClientSpy = spy(kafkaProducerClient);
    }

    @Test
    public void testSendDataSync() throws JsonProcessingException {

        Exception exception = null;
        JsonNode jsonNode = Utils.OBJECT_MAPPER.readTree(CDC_EVENT);

        Future<RecordMetadata> recordMetadataFuture = mock(Future.class);
        doReturn(recordMetadataFuture).when(kafkaProducer).send(any());

        try {
            kafkaProducerClientSpy.sendDataSync(jsonNode);
        } catch (final Exception ex) {
            exception = ex;
        }

        assertNull(exception);
    }

    @Test
    public void testSendDataSyncException() throws JsonProcessingException {
        LogCaptor logCaptor = LogCaptor.forClass(KafkaProducerClientImpl.class);

        Exception exception = null;
        JsonNode jsonNode = Utils.OBJECT_MAPPER.readTree(CDC_EVENT);

        try {
            Future<RecordMetadata> recordMetadataFuture = mock(Future.class);
            doReturn(recordMetadataFuture).when(kafkaProducer).send(any());
            when(recordMetadataFuture.get()).thenThrow(new InterruptedException("Test Exception"));
            kafkaProducerClientSpy.sendDataSync(jsonNode);
        } catch (final Exception ex) {
            exception = ex;
        }

        assertNull(exception);

        assertTrue(logCaptor
                .getErrorLogs()
                .get(logCaptor.getErrorLogs().size() - 1)
                .contains("Error occurred, while producing event to kafka. "));
    }

    @Test
    public void testSendData() throws JsonProcessingException {

        Exception exception = null;
        JsonNode jsonNode = Utils.OBJECT_MAPPER.readTree(CDC_EVENT);

        Future<RecordMetadata> recordMetadataFuture = mock(Future.class);
        doReturn(recordMetadataFuture).when(kafkaProducer).send(any());

        try {
            kafkaProducerClientSpy.sendData(jsonNode);
        } catch (final Exception ex) {
            exception = ex;
        }

        assertNull(exception);
    }

    @Test
    public void testSendDataException() throws JsonProcessingException {

        LogCaptor logCaptor = LogCaptor.forClass(KafkaProducerClientImpl.class);

        Exception exception = null;
        JsonNode jsonNode = Utils.OBJECT_MAPPER.readTree(CDC_EVENT);

        try {
            Future<RecordMetadata> recordMetadataFuture = mock(Future.class);
            doReturn(recordMetadataFuture).when(kafkaProducer).send(any());
            when(kafkaProducer.send(any())).thenThrow(new RuntimeException("Test Exception"));
            kafkaProducerClientSpy.sendData(jsonNode);
        } catch (final Exception ex) {
            exception = ex;
        }

        assertNull(exception);

        assertTrue(logCaptor
                .getErrorLogs()
                .get(logCaptor.getErrorLogs().size() - 1)
                .contains("Error occurred, while producing event to kafka. "));
    }

    @Test
    public void testSendDataASync() throws JsonProcessingException {

        Exception exception = null;
        JsonNode jsonNode = Utils.OBJECT_MAPPER.readTree(CDC_EVENT);

        Future<RecordMetadata> recordMetadataFuture = mock(Future.class);
        doReturn(recordMetadataFuture).when(kafkaProducer).send(any(), any());

        try {
            kafkaProducerClientSpy.sendDataAsync(jsonNode);
        } catch (final Exception ex) {
            exception = ex;
        }

        assertNull(exception);
    }

    @Test
    public void testSendDataASyncException() throws JsonProcessingException {
        LogCaptor logCaptor = LogCaptor.forClass(KafkaProducerClientImpl.class);

        Exception exception = null;
        JsonNode jsonNode = Utils.OBJECT_MAPPER.readTree(CDC_EVENT);

        try {
            Future<RecordMetadata> recordMetadataFuture = mock(Future.class);
            doReturn(recordMetadataFuture).when(kafkaProducer).send(any(), any());
            when(kafkaProducer.send(any(), any())).thenThrow(new RuntimeException("Test Exception"));
            kafkaProducerClientSpy.sendDataAsync(jsonNode);
        } catch (final Exception ex) {
            exception = ex;
        }

        assertNull(exception);

        assertTrue(logCaptor
                .getErrorLogs()
                .get(logCaptor.getErrorLogs().size() - 1)
                .contains("Error occurred, while producing event to kafka. "));

    }
}
