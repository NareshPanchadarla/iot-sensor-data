package com.relay42.iot.sensor.data.service;

import com.relay42.iot.sensor.data.dto.SensorGroupDTO;
import com.relay42.iot.sensor.data.dto.SensorGroupReadingsRequestDTO;
import com.relay42.iot.sensor.data.dto.SensorReadingsDTO;
import com.relay42.iot.sensor.data.dto.SensorReadingsProjection;
import com.relay42.iot.sensor.data.dto.SensorReadingsRequestDTO;
import com.relay42.iot.sensor.data.dto.SensorReadingsResponseDTO;
import com.relay42.iot.sensor.data.enums.SensorType;
import com.relay42.iot.sensor.data.exception.GeneralServerException;
import com.relay42.iot.sensor.data.exception.SensorNotFoundException;
import com.relay42.iot.sensor.data.repository.SensorReadingRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SensorServiceImplTest {

    @Mock
    private SensorReadingRepository sensorReadingRepository;

    @InjectMocks
    private SensorServiceImpl sensorService;

    private SensorReadingsRequestDTO sensorReadingsRequestDTO;
    private SensorReadingsProjection sensorReadingsProjection;
    @Mock
    private SensorGroupReadingsRequestDTO sensorGroupReadingsRequestDTO;
    @Mock
    private SensorGroupDTO sensorGroupDTO;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        sensorReadingsRequestDTO = new SensorReadingsRequestDTO(
                "Sensor_ID",
                SensorType.THERMOSTAT,
                LocalDateTime.parse("2025-01-01T00:00:00"),
                LocalDateTime.parse("2025-01-01T23:59:59"));

        // Set up mock projection
        sensorReadingsProjection = mock(SensorReadingsProjection.class);
        when(sensorReadingsProjection.getMinValue()).thenReturn(5.0);
        when(sensorReadingsProjection.getMaxValue()).thenReturn(25.0);
        when(sensorReadingsProjection.getAvgValue()).thenReturn(15.0);
        when(sensorReadingsProjection.getMedianValue()).thenReturn(15.0);
    }

    @Test
    public void testGetSensorReadings_Success() {
        // Arrange
        when(sensorReadingRepository.findSensorReadings(
                sensorReadingsRequestDTO.getSensorId(),
                sensorReadingsRequestDTO.getSensorType().name(),
                sensorReadingsRequestDTO.getStartTime(),
                sensorReadingsRequestDTO.getEndTime()
        )).thenReturn(sensorReadingsProjection);

        // Act
        SensorReadingsResponseDTO response = sensorService.getSensorReadings(sensorReadingsRequestDTO);

        // Assert
        assertNotNull(response);
        assertThat(response.getMin()).isEqualTo(5.0);
        assertThat(response.getMax()).isEqualTo(25.0);
        assertThat(response.getAverage()).isEqualTo(15.0);
        assertThat(response.getMedian()).isEqualTo(15.0);
    }

    @Test
    public void testGetSensorReadings_NoDataFound() {
        // Arrange
        when(sensorReadingRepository.findSensorReadings(
                sensorReadingsRequestDTO.getSensorId(),
                sensorReadingsRequestDTO.getSensorType().name(),
                sensorReadingsRequestDTO.getStartTime(),
                sensorReadingsRequestDTO.getEndTime()
        )).thenReturn(null);

        // Act
        SensorNotFoundException thrown = assertThrows(SensorNotFoundException.class, () -> sensorService.getSensorReadings(sensorReadingsRequestDTO));

        // Assert
        assertEquals("No sensor found with this sensor details", thrown.getMessage());
    }

    @Test
    void testGetSensorReadings_UnexpectedError() {
        // Arrange
        when(sensorReadingRepository.findSensorReadings(
                sensorReadingsRequestDTO.getSensorId(),
                sensorReadingsRequestDTO.getSensorType().name(),
                sensorReadingsRequestDTO.getStartTime(),
                sensorReadingsRequestDTO.getEndTime()))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> sensorService.getSensorReadings(sensorReadingsRequestDTO));
        assertEquals("An unexpected error occurred. Please try again later.", thrown.getMessage());
    }

    @Test
    public void testProcessSensorGroup_withValidReadings() {
        // Arrange
        String sensorId = "sensor123";
        SensorType sensorType = SensorType.HEART_RATE;
        when(sensorGroupDTO.getSensorId()).thenReturn(sensorId);
        when(sensorGroupDTO.getSensorType()).thenReturn(sensorType);
        when(sensorGroupReadingsRequestDTO.getStartTime()).thenReturn(LocalDateTime.parse("2025-01-01T00:00:00"));
        when(sensorGroupReadingsRequestDTO.getEndTime()).thenReturn(LocalDateTime.parse("2025-01-01T23:59:59"));
        when(sensorReadingRepository.findSensorReadings(
                sensorId,
                sensorType.name(),
                LocalDateTime.parse("2025-01-01T00:00:00"),
                LocalDateTime.parse("2025-01-01T23:59:59"))
        ).thenReturn(sensorReadingsProjection);

        when(sensorReadingsProjection.getMinValue()).thenReturn(10.0);
        when(sensorReadingsProjection.getMaxValue()).thenReturn(20.0);
        when(sensorReadingsProjection.getAvgValue()).thenReturn(15.0);
        when(sensorReadingsProjection.getMedianValue()).thenReturn(14.0);

        // Act
        SensorReadingsDTO result = sensorService.processSensorGroup(sensorGroupDTO, sensorGroupReadingsRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(sensorId, result.getSensorId());
        assertEquals(sensorType, result.getSensorType());
        assertNotNull(result.getReadings());
        assertEquals(10.0, result.getReadings().getMin());
        assertEquals(20.0, result.getReadings().getMax());
        assertEquals(15.0, result.getReadings().getAvg());
        assertEquals(14.0, result.getReadings().getMedian());
    }


    @Test
    public void testProcessSensorGroup_withNullReadings() {
        // Arrange
        String sensorId = "sensor123";
        SensorType sensorType = SensorType.HEART_RATE;
        when(sensorGroupDTO.getSensorId()).thenReturn(sensorId);
        when(sensorGroupDTO.getSensorType()).thenReturn(sensorType);
        when(sensorGroupReadingsRequestDTO.getStartTime()).thenReturn(LocalDateTime.parse("2025-01-01T00:00:00"));
        when(sensorGroupReadingsRequestDTO.getEndTime()).thenReturn(LocalDateTime.parse("2025-01-01T23:59:59"));
        when(sensorReadingRepository.findSensorReadings(
                sensorId,
                sensorType.name(),
                LocalDateTime.parse("2025-01-01T00:00:00"),
                LocalDateTime.parse("2025-01-01T23:59:59"))
        ).thenReturn(sensorReadingsProjection);

        // Simulating an empty projection (no data found)
        when(sensorReadingsProjection.getMinValue()).thenReturn(null);
        when(sensorReadingsProjection.getMaxValue()).thenReturn(null);
        when(sensorReadingsProjection.getAvgValue()).thenReturn(null);
        when(sensorReadingsProjection.getMedianValue()).thenReturn(null);

        // Act
        SensorReadingsDTO result = sensorService.processSensorGroup(sensorGroupDTO, sensorGroupReadingsRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(sensorId, result.getSensorId());
        assertEquals(sensorType, result.getSensorType());
        assertNull(result.getReadings());  // Readings should be null as no data was found
    }

    @Test
    public void testProcessSensorGroup_withDatabaseError() {
        // Arrange
        String sensorId = "sensor123";
        SensorType sensorType = SensorType.HEART_RATE;
        when(sensorGroupDTO.getSensorId()).thenReturn(sensorId);
        when(sensorGroupDTO.getSensorType()).thenReturn(sensorType);
        when(sensorGroupReadingsRequestDTO.getStartTime()).thenReturn(LocalDateTime.parse("2025-01-01T00:00:00"));
        when(sensorGroupReadingsRequestDTO.getEndTime()).thenReturn(LocalDateTime.parse("2025-01-01T23:59:59"));

        // Simulating an exception in the database call
        when(sensorReadingRepository.findSensorReadings(
                sensorId,
                sensorType.name(),
                LocalDateTime.parse("2025-01-01T00:00:00"),
                LocalDateTime.parse("2025-01-01T23:59:59"))
        ).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        Exception exception = assertThrows(GeneralServerException.class, () -> sensorService.processSensorGroup(sensorGroupDTO, sensorGroupReadingsRequestDTO));
        assertEquals("An unexpected error occurred while processing SensorGroup. Please try again later.", exception.getMessage());
    }


}
