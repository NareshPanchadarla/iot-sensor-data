package com.relay42.iot.sensor.data.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.relay42.iot.sensor.data.config.CustomTestSecurityConfig;
import com.relay42.iot.sensor.data.dto.SensorReadingsRequestDTO;
import com.relay42.iot.sensor.data.dto.SensorReadingsResponseDTO;
import com.relay42.iot.sensor.data.exception.GeneralServerException;
import com.relay42.iot.sensor.data.security.CustomUserDetailsService;
import com.relay42.iot.sensor.data.service.SensorService;
import com.relay42.iot.sensor.data.util.JwtTokenUtil;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.relay42.iot.sensor.data.enums.SensorType.HEART_RATE;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(CustomTestSecurityConfig.class)
public class SensorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SensorService sensorService;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @InjectMocks
    private SensorController sensorController;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    public void testGetSensorReadings_Success() throws Exception {
        // Arrange
        SensorReadingsRequestDTO requestDTO = new SensorReadingsRequestDTO("sensor1", HEART_RATE, LocalDateTime.parse("2025-01-01T00:00:00"), LocalDateTime.parse("2025-01-01T23:59:59"));

        SensorReadingsResponseDTO responseDTO = new SensorReadingsResponseDTO(10.0, 100.0, 55.0, 50.0);

        when(sensorService.getSensorReadings(any(SensorReadingsRequestDTO.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/v1/sensors/readings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.min").value(10.0))
                .andExpect(jsonPath("$.max").value(100.0))
                .andExpect(jsonPath("$.average").value(55.0))
                .andExpect(jsonPath("$.median").value(50.0));

        verify(sensorService, times(1)).getSensorReadings(any(SensorReadingsRequestDTO.class)); // Verify service method call
    }


    @Test
    public void testGetSensorReadings_ServiceError() throws Exception {
        // Arrange
        SensorReadingsRequestDTO requestDTO = new SensorReadingsRequestDTO("sensor2", HEART_RATE, LocalDateTime.parse("2025-01-01T00:00:00"), LocalDateTime.parse("2025-01-01T23:59:59"));

        when(sensorService.getSensorReadings(any(SensorReadingsRequestDTO.class))).thenThrow(new GeneralServerException("Unexpected error"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/sensors/readings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isInternalServerError()) // 500 due to service error
                .andExpect(jsonPath("$.message").value("Unexpected error"));
    }
}
