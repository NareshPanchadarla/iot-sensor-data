package com.relay42.iot.sensor.data.dto;

public interface SensorReadingsProjection {
    Double getMinValue();
    Double getMaxValue();
    Double getAvgValue();
    Double getMedianValue();
}
