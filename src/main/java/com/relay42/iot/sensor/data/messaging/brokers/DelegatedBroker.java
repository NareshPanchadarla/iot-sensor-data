package com.relay42.iot.sensor.data.messaging.brokers;

import com.relay42.iot.sensor.data.dto.SensorReadingDTO;
import java.util.List;

public class DelegatedBroker implements EventBroker {

    private final List<EventBroker> brokers;

    public DelegatedBroker(EventBroker... brokers) {
        this.brokers = List.of(brokers);
    }

    @Override
    public void sendIoTSensorReadingsEvent(SensorReadingDTO sensorReadingDTO) {
        brokers.forEach(broker -> broker.sendIoTSensorReadingsEvent(sensorReadingDTO));
    }
}
