package com.relay42.iot.sensor.data.messaging.brokers;

import com.relay42.iot.sensor.data.dto.SensorReadingDTO;

public interface EventBroker {

    void sendIoTSensorReadingsEvent(SensorReadingDTO sensorReadingDTO);
}
