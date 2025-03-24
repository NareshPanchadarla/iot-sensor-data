package com.relay42.iot.sensor.data.messaging.brokers;

import com.relay42.iot.sensor.data.dto.SensorReadingDTO;
import com.relay42.iot.sensor.data.enums.SensorType;
import com.relay42.iot.sensor.data.messaging.IoTSensorEventPublisher;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class IoTSensorEventPublisherTest {

    @Mock
    private EventBroker eventBroker;

    @InjectMocks
    private IoTSensorEventPublisher sensorEventPublisher;

    private SensorReadingDTO sensorReadingDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sensorReadingDTO = new SensorReadingDTO();
        sensorReadingDTO.setSensorId("sensor-123");
        sensorReadingDTO.setSensorType(SensorType.THERMOSTAT);
        sensorReadingDTO.setReadingValue(new BigDecimal(20));
        sensorReadingDTO.setUnit("C");
    }

    @Test
    void testSendIoTSensorReadingsEvent() {
        sensorEventPublisher.sendIoTSensorReadingsEvent(sensorReadingDTO);
        verify(eventBroker, times(1)).sendIoTSensorReadingsEvent(sensorReadingDTO);
    }
}

