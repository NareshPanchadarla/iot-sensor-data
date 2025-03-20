package com.relay42.iot.sensor.data.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class SensorGroupReadingsRequestDTO {
    @NotNull(message = "Sensor group cannot be null")
    @Valid
    private List<SensorGroupDTO> sensorGroup;

    @NotNull(message = "Start time cannot be null")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;

    @NotNull(message = "End time cannot be null")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;


    @AssertTrue(message = "End time must be after start time")
    public boolean isTimeRangeValid() {
        return endTime.isAfter(startTime);
    }
}



