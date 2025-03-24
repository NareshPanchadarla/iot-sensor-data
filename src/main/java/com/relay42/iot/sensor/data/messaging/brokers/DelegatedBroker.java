package com.relay42.iot.sensor.data.messaging.brokers;

import com.relay42.iot.sensor.data.dto.SensorReadingDTO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DelegatedBroker implements EventBroker {

    private final List<EventBroker> brokers;

    public DelegatedBroker(EventBroker... brokers) {
        this.brokers = List.of(brokers);
    }

    private static final Logger logger = LoggerFactory.getLogger(DelegatedBroker.class);

    @Override
    public void sendIoTSensorReadingsEvent(SensorReadingDTO sensorReadingDTO) {
        for (EventBroker broker : brokers) {
            try {
                broker.sendIoTSensorReadingsEvent(sensorReadingDTO);
            } catch (Exception e) {
                logger.warn("Broker {} failed to send event: {}", broker.getClass().getSimpleName(), e.getMessage());
            }
        }
    }
}
