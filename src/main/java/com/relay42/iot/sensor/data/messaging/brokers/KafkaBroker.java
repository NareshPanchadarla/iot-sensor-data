package com.relay42.iot.sensor.data.messaging.brokers;

import com.relay42.iot.sensor.data.avro.IoTSensorEventKafka;
import com.relay42.iot.sensor.data.avro.IoTSensorEventKey;
import com.relay42.iot.sensor.data.avro.IoTSensorEventTypeKafka;
import com.relay42.iot.sensor.data.avro.IoTSensorReadings;
import com.relay42.iot.sensor.data.avro.SensorType;
import com.relay42.iot.sensor.data.dto.SensorReadingDTO;
import com.relay42.iot.sensor.data.exception.GeneralServerException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import static com.relay42.iot.sensor.data.config.IoTSensorReadingsEventPublishConfig.IOT_SENSOR_READINGS_TOPIC;

@RequiredArgsConstructor
@Slf4j
public class KafkaBroker implements EventBroker {

    private final KafkaTemplate<IoTSensorEventKey, IoTSensorEventKafka> iotSensorReadingsTemplate;

    private static final Logger logger = LoggerFactory.getLogger(KafkaBroker.class);

    @Override
    public void sendIoTSensorReadingsEvent(SensorReadingDTO sensorReadingDTO) {
        try {
            IoTSensorReadings sensorReadingsEvent = new IoTSensorReadings();
            sensorReadingsEvent.setSensorId(sensorReadingDTO.getSensorId());
            sensorReadingsEvent.setSensorType(SensorType.valueOf(sensorReadingDTO.getSensorType().name()));
            sensorReadingsEvent.setReadingValue(bigDecimalToAvroBytes(sensorReadingDTO.getReadingValue(), 10, 4));
            sensorReadingsEvent.setUnit(sensorReadingDTO.getUnit());
            sensorReadingsEvent.setReadingAt(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());

            IoTSensorEventKafka ioTSensorEventKafka = IoTSensorEventKafka.newBuilder()
                    .setEventType(IoTSensorEventTypeKafka.SENSOR_READINGS)
                    .setIotSensorReadings(sensorReadingsEvent)
                    .build();
            IoTSensorEventKey kafkaKey = createKey(sensorReadingDTO.getSensorId());
            sendEvent(kafkaKey, ioTSensorEventKafka);
        } catch (Exception e) {
            logger.error("Failed to send IoTSensorReadingsEvent for sensorId {}", sensorReadingDTO.getSensorId(), e);
            throw new GeneralServerException("Error while sending kafka event" + e);
        }
    }

    public static ByteBuffer bigDecimalToAvroBytes(BigDecimal bigDecimal, int precision, int scale) {
        bigDecimal = bigDecimal.setScale(scale, RoundingMode.HALF_UP);
        byte[] byteArray = bigDecimal.unscaledValue().toByteArray();
        return ByteBuffer.wrap(byteArray);
    }


    private void sendEvent(IoTSensorEventKey kafkaKey, IoTSensorEventKafka event) {
        try {
            CompletableFuture<SendResult<IoTSensorEventKey, IoTSensorEventKafka>> future =
                    iotSensorReadingsTemplate.send(IOT_SENSOR_READINGS_TOPIC, kafkaKey, event);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.info("Message sent successfully  {}", result.getRecordMetadata());
                } else {
                    logger.info("Error sending message: {}", ex.getMessage());
                }
            });
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    private IoTSensorEventKey createKey(String sensorId) {
        return IoTSensorEventKey.newBuilder().setSensorId(sensorId).build();
    }
}
