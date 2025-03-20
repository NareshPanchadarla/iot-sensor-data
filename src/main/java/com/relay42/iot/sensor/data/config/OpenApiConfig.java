package com.relay42.iot.sensor.data.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sensor Statistics API")
                        .version("1.0")
                        .description("API for retrieving sensor statistics such as min, max, avg, and median readings."));
    }
}
