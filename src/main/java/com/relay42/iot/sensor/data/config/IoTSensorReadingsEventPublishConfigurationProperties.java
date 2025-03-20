package com.relay42.iot.sensor.data.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class IoTSensorReadingsEventPublishConfigurationProperties {

    @Value("${iot.sensor.readings.publish.kafka.enabled}")
    public boolean kafkaPublishEnabled;
}
