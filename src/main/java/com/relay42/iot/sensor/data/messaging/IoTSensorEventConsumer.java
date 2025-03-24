package com.relay42.iot.sensor.data.messaging;

import com.relay42.iot.sensor.data.avro.IoTSensorEventKafka;
import com.relay42.iot.sensor.data.avro.IoTSensorEventTypeKafka;
import com.relay42.iot.sensor.data.avro.IoTSensorReadings;
import com.relay42.iot.sensor.data.converter.AvroToEntityConverter;
import com.relay42.iot.sensor.data.entity.SensorReading;
import com.relay42.iot.sensor.data.service.SensorService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class IoTSensorEventConsumer {

    private final SensorService sensorService;

    @KafkaListener(
            topics = "sensor-data",
            groupId = "sensor-data.group"
    )

    public void sensorEventHandler(@Payload IoTSensorEventKafka message) {
        if (message == null || message.getEventType() == null) {
            log.warn("Received null or invalid message, ignoring...");
            return;
        }
        if (!IoTSensorEventTypeKafka.SENSOR_READINGS.equals(message.getEventType())) {
            return;
        }
        IoTSensorReadings sensorReadings = (IoTSensorReadings) message.getIotSensorReadings();
        SensorReading sensorReading = AvroToEntityConverter.convertAvroToEntity(sensorReadings);
        sensorService.saveSensorReading(sensorReading);
    }
}
