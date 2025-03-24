package com.relay42.iot.sensor.data.messaging.brokers;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.relay42.iot.sensor.data.dto.SensorReadingDTO;
import com.relay42.iot.sensor.data.enums.SensorType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
class DelegatedBrokerTest {

    @Mock
    private EventBroker broker1;

    @Mock
    private EventBroker broker2;

    private DelegatedBroker delegatedBroker;

    private SensorReadingDTO sensorReadingDTO;

    @BeforeEach
    void setUp() {
        delegatedBroker = new DelegatedBroker(broker1, broker2);
        sensorReadingDTO = new SensorReadingDTO();
        sensorReadingDTO.setSensorId("sensor-123");
        sensorReadingDTO.setSensorType(SensorType.THERMOSTAT);
        sensorReadingDTO.setReadingValue(BigDecimal.valueOf(25.1234));
        sensorReadingDTO.setUnit("C");
    }

    @Test
    void testSendIoTSensorReadingsEvent_CallsAllBrokers() {
        delegatedBroker.sendIoTSensorReadingsEvent(sensorReadingDTO);

        verify(broker1, times(1)).sendIoTSensorReadingsEvent(sensorReadingDTO);
        verify(broker2, times(1)).sendIoTSensorReadingsEvent(sensorReadingDTO);
    }

    @Test
    void testSendIoTSensorReadingsEvent_OneBrokerFails() {
        doThrow(new RuntimeException("Broker1 failure")).when(broker1).sendIoTSensorReadingsEvent(sensorReadingDTO);

        assertDoesNotThrow(() -> delegatedBroker.sendIoTSensorReadingsEvent(sensorReadingDTO));

        verify(broker1, times(1)).sendIoTSensorReadingsEvent(sensorReadingDTO);
        verify(broker2, times(1)).sendIoTSensorReadingsEvent(sensorReadingDTO);
    }
}

