package com.relay42.iot.sensor.data.messaging;

import com.relay42.iot.sensor.data.dto.SensorReadingDTO;
import com.relay42.iot.sensor.data.messaging.brokers.EventBroker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IoTSensorEventPublisher {

    private final EventBroker eventBroker;

    public void sendIoTSensorReadingsEvent(SensorReadingDTO sensorReadingDTO) {
        eventBroker.sendIoTSensorReadingsEvent(sensorReadingDTO);
    }
}
