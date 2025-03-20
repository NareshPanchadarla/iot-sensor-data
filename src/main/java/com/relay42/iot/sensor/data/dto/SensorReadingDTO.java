package com.relay42.iot.sensor.data.dto;

import com.relay42.iot.sensor.data.enums.SensorType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class SensorReadingDTO {
    private String sensorId;
    private SensorType sensorType;
    private LocalDateTime readingAt;
    private BigDecimal readingValue;
    private String unit;
}
