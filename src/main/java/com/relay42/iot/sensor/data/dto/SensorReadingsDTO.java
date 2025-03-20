package com.relay42.iot.sensor.data.dto;

import com.relay42.iot.sensor.data.enums.SensorType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SensorReadingsDTO {
    private String sensorId;
    private SensorType sensorType;
    private ReadingsDTO readings;
}
