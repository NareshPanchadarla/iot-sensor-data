package com.relay42.iot.sensor.data.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SensorMetaDataDTO {
    private List<String> unavailableSensorIds;
}
