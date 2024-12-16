package konnect.kafka.consumer;

import java.time.Duration;
import java.util.concurrent.Executors;
import konnect.config.AppConfig;
import konnect.config.ConfigReader;
import nl.altindag.log.LogCaptor;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

public class KafkaConsumerClientImplTest {
    private KafkaConsumerClientImpl kafkaConsumerClient;
    private KafkaConsumerClientImpl kafkaConsumerClientSpy;
    private KafkaConsumer<String, String> kafkaConsumer;


    @Before
    public void mockSetup() {
        AppConfig appConfig = new AppConfig(new ConfigReader());
        kafkaConsumer = mock(KafkaConsumer.class);
        kafkaConsumerClient = new KafkaConsumerClientImpl(appConfig);
        ReflectionTestUtils.setField(kafkaConsumerClient, "kafkaConsumer", kafkaConsumer);
        kafkaConsumerClientSpy = spy(kafkaConsumerClient);
        doNothing().when(kafkaConsumerClientSpy).addShutdownHook();
    }

    @Test
    public void testProcessRecords() {

        Exception exception = null;

        try {
            Executors.newSingleThreadScheduledExecutor().submit(() -> {
                kafkaConsumerClientSpy.processEvents();
            });
            kafkaConsumerClientSpy.setClosing();
        } catch (final Exception ex) {
            exception = ex;
        }

        assertNull(exception);
    }

    @Test
    public void testProcessRecordsException() {
        LogCaptor logCaptor = LogCaptor.forClass(KafkaConsumerClientImpl.class);
        Exception exception = null;

        try {
            doThrow(new RuntimeException("Test Exception")).when(kafkaConsumerClientSpy).processBulkEvents(any(Duration.class));
            kafkaConsumerClientSpy.processEvents();
        } catch (final Exception ex) {
            exception = ex;
        }

        assertNull(exception);

        assertTrue(logCaptor
                .getErrorLogs()
                .get(logCaptor.getErrorLogs().size() - 1)
                .contains("Unexpected error"));


        logCaptor = LogCaptor.forClass(KafkaConsumerClientImpl.class);
        try {
            doThrow(new WakeupException()).when(kafkaConsumerClientSpy).processBulkEvents(any(Duration.class));
            kafkaConsumerClientSpy.processEvents();
        } catch (final Exception ex) {
            exception = ex;
        }

        assertNull(exception);

        assertTrue(logCaptor
                .getInfoLogs()
                .get(0)
                .contains("Wake up exception"));
    }

}
