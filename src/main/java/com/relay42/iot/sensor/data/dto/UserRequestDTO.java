package com.relay42.iot.sensor.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;

@Data
@AllArgsConstructor
public class UserRequestDTO {
    @NotBlank(message = "Name should not be empty")
    private String username;

    @NotBlank(message = "Email should not be empty")
    @Email(message = "Enter a valid email")
    private String email;

    @NotNull(message = "Password should not be empty")
    @Size(min = 5, message = "Password should be at least 5 characters")
    private String password;

}
