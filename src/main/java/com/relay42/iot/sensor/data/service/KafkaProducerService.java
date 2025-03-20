package com.relay42.iot.sensor.data.service;

import com.relay42.iot.sensor.data.dto.SensorReadingDTO;

public interface KafkaProducerService {
    void sendMessage(SensorReadingDTO sensorReadingDTO);
}
