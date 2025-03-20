package com.relay42.iot.sensor.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReadingsDTO {
    private Double min;
    private Double max;
    private Double Avg;
    private Double median;
}
