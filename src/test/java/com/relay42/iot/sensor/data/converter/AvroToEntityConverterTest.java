package com.relay42.iot.sensor.data.converter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.relay42.iot.sensor.data.avro.IoTSensorReadings;
import com.relay42.iot.sensor.data.entity.SensorReading;
import com.relay42.iot.sensor.data.enums.SensorType;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AvroToEntityConverterTest {

    private IoTSensorReadings mockIotSensorReadings;

    @BeforeEach
    void setUp() {
        mockIotSensorReadings = mock(IoTSensorReadings.class);
    }

    @Test
    void testConvertAvroToEntity_Success() {
        // Arrange: Mock the IoTSensorReadings object
        when(mockIotSensorReadings.getSensorId()).thenReturn("sensor-001");
        when(mockIotSensorReadings.getSensorType()).thenReturn(com.relay42.iot.sensor.data.avro.SensorType.valueOf(SensorType.THERMOSTAT.name()));
        when(mockIotSensorReadings.getReadingValue()).thenReturn(ByteBuffer.wrap(new byte[] { 0x01, 0x02, 0x03, 0x04 }));
        when(mockIotSensorReadings.getUnit()).thenReturn("Celsius");
        when(mockIotSensorReadings.getReadingAt()).thenReturn(Instant.now().toEpochMilli());

        // Act: Convert IoTSensorReadings to SensorReading
        SensorReading result = AvroToEntityConverter.convertAvroToEntity(mockIotSensorReadings);

        // Assert: Verify that the values were correctly set
        assertNotNull(result);
        assertEquals("sensor-001", result.getSensorId());
        assertEquals(SensorType.THERMOSTAT, result.getSensorType());
        assertEquals(new BigDecimal("1690.9060"), result.getReadingValue()); // Assuming the convertByteToBigDecimal method converts correctly
        assertEquals("Celsius", result.getUnit());
        assertNotNull(result.getReadingAt());
        assertNotNull(result.getCreatedAt());
    }

    @Test
    void testConvertAvroToEntity_NullSensorId() {
        // Arrange: Mock the IoTSensorReadings object
        when(mockIotSensorReadings.getSensorId()).thenReturn(null);
        when(mockIotSensorReadings.getSensorType()).thenReturn(com.relay42.iot.sensor.data.avro.SensorType.valueOf(SensorType.THERMOSTAT.name()));
        when(mockIotSensorReadings.getReadingValue()).thenReturn(ByteBuffer.wrap(new byte[] { 0x05, 0x06, 0x07, 0x08 }));
        when(mockIotSensorReadings.getUnit()).thenReturn("Percent");
        when(mockIotSensorReadings.getReadingAt()).thenReturn(Instant.now().toEpochMilli());

        // Act: Convert IoTSensorReadings to SensorReading
        SensorReading result = AvroToEntityConverter.convertAvroToEntity(mockIotSensorReadings);

        // Assert: Verify the sensorId is correctly handled (null case)
        assertNull(result.getSensorId());
        assertNotNull(result.getSensorType());
    }

    @Test
    void testConvertAvroToEntity_ConvertByteToBigDecimal() {
        // Arrange: Set the expected byte value and mock the method
        byte[] bytes = { 0x01, 0x02, 0x03, 0x04 };
        BigDecimal expectedReadingValue = new BigDecimal("1690.9060");

        // Act: Call the convertByteToBigDecimal method directly
        BigDecimal result = AvroToEntityConverter.convertByteToBigDecimal(bytes, 4);

        // Assert: Verify the value conversion
        assertEquals(expectedReadingValue, result);
    }
}

