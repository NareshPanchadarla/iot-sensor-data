package com.relay42.iot.sensor.data.service;

import com.relay42.iot.sensor.data.dto.SensorGroupReadingsRequestDTO;
import com.relay42.iot.sensor.data.dto.SensorGroupReadingsResponseDTO;
import com.relay42.iot.sensor.data.dto.SensorReadingsRequestDTO;
import com.relay42.iot.sensor.data.dto.SensorReadingsResponseDTO;
import com.relay42.iot.sensor.data.entity.SensorReading;

public interface SensorService {
    SensorReadingsResponseDTO getSensorReadings(SensorReadingsRequestDTO sensorReadingsRequestDTO);
    SensorGroupReadingsResponseDTO getGroupOfSensorReadings(SensorGroupReadingsRequestDTO sensorGroupReadingsRequestDTO);
    void saveSensorReading(SensorReading sensorReading);
}
