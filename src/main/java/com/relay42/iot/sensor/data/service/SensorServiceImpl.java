package com.relay42.iot.sensor.data.service;

import com.relay42.iot.sensor.data.dto.ReadingsDTO;
import com.relay42.iot.sensor.data.dto.SensorGroupDTO;
import com.relay42.iot.sensor.data.dto.SensorGroupReadingsRequestDTO;
import com.relay42.iot.sensor.data.dto.SensorGroupReadingsResponseDTO;
import com.relay42.iot.sensor.data.dto.SensorMetaDataDTO;
import com.relay42.iot.sensor.data.dto.SensorReadingsDTO;
import com.relay42.iot.sensor.data.dto.SensorReadingsProjection;
import com.relay42.iot.sensor.data.dto.SensorReadingsRequestDTO;
import com.relay42.iot.sensor.data.dto.SensorReadingsResponseDTO;
import com.relay42.iot.sensor.data.entity.SensorReading;
import com.relay42.iot.sensor.data.exception.GeneralServerException;
import com.relay42.iot.sensor.data.exception.SensorNotFoundException;
import com.relay42.iot.sensor.data.repository.SensorReadingRepository;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class SensorServiceImpl implements SensorService {

    private final SensorReadingRepository sensorReadingRepository;

    private static final Logger logger = LoggerFactory.getLogger(SensorServiceImpl.class);

    @Override
    public SensorReadingsResponseDTO getSensorReadings(SensorReadingsRequestDTO sensorReadingsRequestDTO) {
        try {
            SensorReadingsProjection sensorReadingsProjection = sensorReadingRepository.findSensorReadings(sensorReadingsRequestDTO.getSensorId(),
                    sensorReadingsRequestDTO.getSensorType().name(), sensorReadingsRequestDTO.getStartTime(), sensorReadingsRequestDTO.getEndTime());

            if (sensorReadingsProjection == null || isEmpty(sensorReadingsProjection)) {
                throw new SensorNotFoundException("No sensor found with this sensor details");
            }

            return new SensorReadingsResponseDTO(sensorReadingsProjection.getMinValue(), sensorReadingsProjection.getMaxValue(),
                    sensorReadingsProjection.getAvgValue(), sensorReadingsProjection.getMedianValue());
        } catch (SensorNotFoundException ex) {
            logger.error("No sensor readings found for sensor ID: {}", sensorReadingsRequestDTO.getSensorId(), ex);
            throw ex;
        } catch (Exception ex) {
            logger.error("Unexpected error, while getting sensor readings for sensor ID: {}", sensorReadingsRequestDTO.getSensorId(), ex);
            throw new GeneralServerException("An unexpected error occurred. Please try again later.");
        }
    }

    @Override
    public SensorGroupReadingsResponseDTO getGroupOfSensorReadings(SensorGroupReadingsRequestDTO sensorGroupReadingsRequestDTO) {
        List<SensorReadingsDTO> listOfSensorReadings = sensorGroupReadingsRequestDTO.getSensorGroup().stream()
                .filter(distinctByKey(SensorGroupDTO::getSensorId))
                .map(sensorGroup -> processSensorGroup(sensorGroup, sensorGroupReadingsRequestDTO))
                .collect(Collectors.toList());

        return new SensorGroupReadingsResponseDTO(listOfSensorReadings, new SensorMetaDataDTO(findSensorIdsWithNullReadings(listOfSensorReadings)));
    }

    @Override
    public void saveSensorReading(SensorReading sensorReading) {
        sensorReadingRepository.save(sensorReading);
    }

    public SensorReadingsDTO processSensorGroup(SensorGroupDTO sensorGroup, SensorGroupReadingsRequestDTO sensorGroupReadingsRequestDTO) {
        SensorReadingsProjection sensorReadingsProjection;
        try {
             sensorReadingsProjection = sensorReadingRepository.findSensorReadings(
                    sensorGroup.getSensorId(),
                    sensorGroup.getSensorType().name(),
                    sensorGroupReadingsRequestDTO.getStartTime(),
                    sensorGroupReadingsRequestDTO.getEndTime()
            );
        } catch (Exception ex) {
            logger.error("Unexpected error in processSensorGroup, while getting sensor readings for sensor ID: {}", sensorGroup.getSensorId(), ex);
            throw new GeneralServerException("An unexpected error occurred while processing SensorGroup. Please try again later.");
        }

        if (isEmpty(sensorReadingsProjection)) {
            return new SensorReadingsDTO(
                    sensorGroup.getSensorId(),
                    sensorGroup.getSensorType(),
                    null
            );
        } else {
            return new SensorReadingsDTO(
                    sensorGroup.getSensorId(),
                    sensorGroup.getSensorType(),
                    new ReadingsDTO(
                            sensorReadingsProjection.getMinValue(),
                            sensorReadingsProjection.getMaxValue(),
                            sensorReadingsProjection.getAvgValue(),
                            sensorReadingsProjection.getMedianValue())
            );
        }
    }

    private List<String> findSensorIdsWithNullReadings(List<SensorReadingsDTO> listOfSensorReadings) {
        return listOfSensorReadings.stream()
                .filter(sensor -> sensor.getReadings() == null)
                .map(SensorReadingsDTO::getSensorId)
                .collect(Collectors.toList());
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    private boolean isEmpty(SensorReadingsProjection projection) {
        return projection.getMinValue() == null &&
                projection.getMaxValue() == null &&
                projection.getAvgValue() == null &&
                projection.getMedianValue() == null;
    }
}
