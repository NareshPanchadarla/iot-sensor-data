package com.relay42.iot.sensor.data.service;

import com.relay42.iot.sensor.data.dto.SensorReadingDTO;
import com.relay42.iot.sensor.data.messaging.IoTSensorEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerServiceImpl implements KafkaProducerService {
    private final IoTSensorEventPublisher ioTSensorEventPublisher;

    @Autowired
    public KafkaProducerServiceImpl(IoTSensorEventPublisher ioTSensorEventPublisher) {
        this.ioTSensorEventPublisher = ioTSensorEventPublisher;
    }

    @Override
    public void sendMessage(SensorReadingDTO sensorReadingDTO) {
        ioTSensorEventPublisher.sendIoTSensorReadingsEvent(sensorReadingDTO);
    }
}
