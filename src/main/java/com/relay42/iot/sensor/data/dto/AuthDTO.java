package com.relay42.iot.sensor.data.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthDTO {
    private String email;
    private String password;
}
