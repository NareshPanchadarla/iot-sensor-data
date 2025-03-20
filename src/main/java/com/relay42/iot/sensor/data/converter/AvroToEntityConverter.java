package com.relay42.iot.sensor.data.converter;

import com.relay42.iot.sensor.data.avro.IoTSensorReadings;
import com.relay42.iot.sensor.data.entity.SensorReading;
import com.relay42.iot.sensor.data.enums.SensorType;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class AvroToEntityConverter {

    public static SensorReading convertAvroToEntity(IoTSensorReadings iotSensorReadings) {

        SensorReading sensorReading = new SensorReading();
        sensorReading.setSensorId(iotSensorReadings.getSensorId());
        sensorReading.setSensorType(SensorType.valueOf(iotSensorReadings.getSensorType().name()));
        sensorReading.setReadingValue(convertByteToBigDecimal(iotSensorReadings.getReadingValue().array(), 4));
        sensorReading.setUnit(iotSensorReadings.getUnit());
        sensorReading.setReadingAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(iotSensorReadings.getReadingAt()), ZoneOffset.UTC));
        sensorReading.setCreatedAt(LocalDateTime.now());

        return sensorReading;
    }

    public static BigDecimal convertByteToBigDecimal(byte[] byteArray, int scale) {
        return new BigDecimal(new java.math.BigInteger(byteArray), scale);
    }
}
