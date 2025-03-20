package com.relay42.iot.sensor.data.repository;

import com.relay42.iot.sensor.data.dto.SensorReadingsProjection;
import com.relay42.iot.sensor.data.entity.SensorReading;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorReadingRepository extends JpaRepository<SensorReading, Long> {

    @Query(value = "SELECT " +
            "MIN(sub.reading_value) AS min_value, " +
            "MAX(sub.reading_value) AS max_value, " +
            "AVG(sub.reading_value) AS avg_value, " +
            "AVG(CASE " +
            "         WHEN sub.row_num IN (FLOOR((sub.total_count + 1) / 2), FLOOR((sub.total_count + 2) / 2)) " +
            "         THEN sub.reading_value " +
            "         ELSE NULL " +
            "     END) AS median_value " +
            "FROM ( " +
            "    SELECT sr.reading_value, " +
            "           ROW_NUMBER() OVER (ORDER BY sr.reading_value) AS row_num, " +
            "           COUNT(*) OVER () AS total_count " +
            "    FROM SensorReading sr " +
            "    WHERE sr.sensor_id = :sensorId " +
            "      AND sr.sensor_type = :sensorType " +
            "      AND sr.reading_at BETWEEN :startTime AND :endTime " +
            ") sub",
            nativeQuery = true)
    SensorReadingsProjection findSensorReadings(@Param("sensorId") String sensorId,
                                                @Param("sensorType") String sensorType,
                                                @Param("startTime") LocalDateTime startTime,
                                                @Param("endTime") LocalDateTime endTime);
}
