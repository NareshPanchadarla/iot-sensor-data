package com.relay42.iot.sensor.data.messaging.brokers;

import static org.mockito.Mockito.*;
import com.relay42.iot.sensor.data.avro.*;
import com.relay42.iot.sensor.data.converter.AvroToEntityConverter;
import com.relay42.iot.sensor.data.entity.SensorReading;
import com.relay42.iot.sensor.data.messaging.IoTSensorEventConsumer;
import com.relay42.iot.sensor.data.service.SensorService;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IoTSensorEventConsumerTest {

    @Mock
    private SensorService sensorService;

    @InjectMocks
    private IoTSensorEventConsumer consumer;

    private IoTSensorEventKafka invalidMessage;



    @Test
    void testSensorEventHandler_InvalidMessage() {
        consumer.sensorEventHandler(invalidMessage);

        verify(sensorService, never()).saveSensorReading(any());
    }
}

