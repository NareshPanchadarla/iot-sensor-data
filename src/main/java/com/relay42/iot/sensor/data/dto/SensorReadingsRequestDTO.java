package com.relay42.iot.sensor.data.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.relay42.iot.sensor.data.enums.SensorType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SensorReadingsRequestDTO {

    @NotBlank(message = "Sensor ID must be provided")
    private String sensorId;
    @NotNull(message = "Sensor type must be provided")
    private SensorType sensorType;
    @NotNull(message = "Start time must be provided")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;
    @NotNull(message = "End time must be provided")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;


   /* @AssertTrue(message = "End time must be after start time")
    private boolean isTimeRangeValid() {
        return endTime == null || startTime == null || endTime.isAfter(startTime);
    }*/
}
