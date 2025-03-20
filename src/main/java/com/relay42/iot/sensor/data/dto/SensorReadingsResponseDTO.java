package com.relay42.iot.sensor.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SensorReadingsResponseDTO {
    private Double min;
    private Double max;
    private Double average;
    private Double median;
}
