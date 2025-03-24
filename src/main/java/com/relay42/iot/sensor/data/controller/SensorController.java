package com.relay42.iot.sensor.data.controller;

import com.relay42.iot.sensor.data.dto.SensorGroupReadingsRequestDTO;
import com.relay42.iot.sensor.data.dto.SensorGroupReadingsResponseDTO;
import com.relay42.iot.sensor.data.dto.SensorReadingsDTO;
import com.relay42.iot.sensor.data.dto.SensorReadingsRequestDTO;
import com.relay42.iot.sensor.data.dto.SensorReadingsResponseDTO;
import com.relay42.iot.sensor.data.exception.SensorNotFoundException;
import com.relay42.iot.sensor.data.service.SensorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sensors")
@Tag(name = "Sensor Controller", description = "APIs for getting sensor readings")
public class SensorController {
    private final SensorService sensorService;

    @Autowired
    public SensorController(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    @Operation(
            summary = "Get sensor readings",
            description = "Retrieve min, max, avg, and median readings for a specified sensors within a specified timeframe.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful response",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SensorReadingsResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Sensor not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    @PostMapping("/readings")
    public ResponseEntity<SensorReadingsResponseDTO> getSensorReadings(@Valid @RequestBody SensorReadingsRequestDTO sensorReadingsRequestDTO) {
        SensorReadingsResponseDTO sensorReadingsResponseDTO = sensorService.getSensorReadings(sensorReadingsRequestDTO);
        return ResponseEntity.ok(sensorReadingsResponseDTO);
    }

    @Operation(
            summary = "Get group of sensor readings",
            description = "Retrieve min, max, avg, and median readings for a specified sensors within a specified timeframe.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful response",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SensorGroupReadingsResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "No sensors found with these sensor group details"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    @PostMapping("/readings/group")
    public ResponseEntity<SensorGroupReadingsResponseDTO> getGroupOfSensorReadings(@Valid @RequestBody SensorGroupReadingsRequestDTO sensorGroupReadingsRequestDTO) {
        SensorGroupReadingsResponseDTO sensorGroupReadingsResponse = sensorService.getGroupOfSensorReadings(sensorGroupReadingsRequestDTO);
        if (sensorGroupReadingsResponse.getSensorGroupReadings().stream().map(SensorReadingsDTO::getReadings).allMatch(Objects::isNull)) {
            throw new SensorNotFoundException("No sensors found with these sensor group details");
        }
        return ResponseEntity.ok(sensorGroupReadingsResponse);
    }

}
