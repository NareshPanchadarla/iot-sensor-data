package com.relay42.iot.sensor.data.entity;

import com.relay42.iot.sensor.data.enums.SensorType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

@Getter
@Setter
@Entity
@Table(name = "SensorReading")
public class SensorReading {

    @Id
    @Column(name = "Id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Sensor_Id", nullable = false)
    private String sensorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "Sensor_Type", nullable = false)
    private SensorType sensorType;

    @Column(name = "Reading_Value", precision = 10, scale = 4, nullable = false)
    private BigDecimal readingValue;

    @Column(name = "Unit", nullable = false)

    private String unit;

    @Column(name = "Reading_At", nullable = false)
    private LocalDateTime readingAt;

    @Column(name = "Created_At", nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;
}
