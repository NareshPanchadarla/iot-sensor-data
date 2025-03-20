package com.relay42.iot.sensor.data.dto;

import com.relay42.iot.sensor.data.enums.SensorType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SensorGroupDTO {

    @NotBlank(message = "Sensor ID must be provided")
    private String sensorId;

    @NotNull(message = "Sensor type must be provided")
    private SensorType sensorType;
}
