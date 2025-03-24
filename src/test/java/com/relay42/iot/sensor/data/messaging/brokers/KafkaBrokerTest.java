package com.relay42.iot.sensor.data.messaging.brokers;

import com.relay42.iot.sensor.data.avro.IoTSensorEventKafka;
import com.relay42.iot.sensor.data.avro.IoTSensorEventKey;
import com.relay42.iot.sensor.data.dto.SensorReadingDTO;
import com.relay42.iot.sensor.data.enums.SensorType;
import com.relay42.iot.sensor.data.exception.GeneralServerException;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaBrokerTest {

    @Mock
    private KafkaTemplate<IoTSensorEventKey, IoTSensorEventKafka> kafkaTemplate;

    @InjectMocks
    private KafkaBroker kafkaBroker;

    private SensorReadingDTO sensorReadingDTO;

    @BeforeEach
    void setUp() {
        sensorReadingDTO = new SensorReadingDTO();
        sensorReadingDTO.setSensorId("sensor-123");
        sensorReadingDTO.setSensorType(SensorType.THERMOSTAT);
        sensorReadingDTO.setReadingValue(BigDecimal.valueOf(25.1234));
        sensorReadingDTO.setUnit("C");
    }

    @Test
    void testSendIoTSensorReadingsEvent_Success() {
        // Mock Kafka behavior
        CompletableFuture<SendResult<IoTSensorEventKey, IoTSensorEventKafka>> future = new CompletableFuture<>();
        future.complete(new SendResult<>(null, new RecordMetadata(null, 0, 0, 0, 0L, 0, 0)));
        when(kafkaTemplate.send(anyString(), any(), any())).thenReturn(future);

        assertDoesNotThrow(() -> kafkaBroker.sendIoTSensorReadingsEvent(sensorReadingDTO));
        verify(kafkaTemplate, times(1)).send(anyString(), any(), any());
    }

    @Test
    void testSendIoTSensorReadingsEvent_Failure() {
        // Mock Kafka failure
        when(kafkaTemplate.send(anyString(), any(), any())).thenThrow(new RuntimeException("Kafka error"));

        assertThrows(GeneralServerException.class, () -> kafkaBroker.sendIoTSensorReadingsEvent(sensorReadingDTO));
    }
}
