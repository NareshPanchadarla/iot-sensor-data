package com.relay42.iot.sensor.data.config;

import com.relay42.iot.sensor.data.avro.IoTSensorEventKafka;
import com.relay42.iot.sensor.data.avro.IoTSensorEventKey;
import com.relay42.iot.sensor.data.messaging.brokers.DelegatedBroker;
import com.relay42.iot.sensor.data.messaging.brokers.EventBroker;
import com.relay42.iot.sensor.data.messaging.brokers.KafkaBroker;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@RequiredArgsConstructor
public class IoTSensorReadingsEventPublishConfig {

    public static final String IOT_SENSOR_READINGS_TOPIC = "sensor-data";

    @Value("${iot.sensor.readings.publish.kafka.partitionCount:12}")
    private int partitionsCount;

    private final IoTSensorReadingsEventPublishConfigurationProperties configurationProperties;


    @Bean
    public NewTopic setupIOTSensorReadingsTopic() {
        return TopicBuilder
                .name(IOT_SENSOR_READINGS_TOPIC)
                .partitions(partitionsCount)
                .build();
    }

    @Bean
    public EventBroker eventBroker(KafkaTemplate<IoTSensorEventKey, IoTSensorEventKafka> ioTSensorReadingsTemplate) {
        List<EventBroker> brokers = new ArrayList<>();
        if (configurationProperties.isKafkaPublishEnabled()) {
            brokers.add(new KafkaBroker(ioTSensorReadingsTemplate));
        }
        return new DelegatedBroker(brokers.toArray(EventBroker[]::new));
    }


}
