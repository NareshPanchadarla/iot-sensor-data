package com.relay42.iot.sensor.data.controller;

import com.relay42.iot.sensor.data.dto.SensorReadingDTO;
import com.relay42.iot.sensor.data.service.KafkaProducerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/sensors")
@RestController
@Tag(name = "Kafka Controller", description = "APIs for sending messages to Kafka")
public class KafkaController {

    private final KafkaProducerService kafkaProducerService;

    @Autowired
    public KafkaController(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }


    @Operation(summary = "Send Sensor Reading to Kafka", description = "Publishes a sensor reading message to Kafka topic.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message successfully sent to Kafka"),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    @PostMapping("/send")
    public String sendKafkaMessage(@RequestBody SensorReadingDTO sensorReadingDTO) {
        kafkaProducerService.sendMessage(sensorReadingDTO);
        return "Message sent to Kafka: ";
    }

}
