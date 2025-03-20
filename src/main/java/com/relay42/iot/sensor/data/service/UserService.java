package com.relay42.iot.sensor.data.service;

import com.relay42.iot.sensor.data.dto.UserRequestDTO;
import com.relay42.iot.sensor.data.dto.UserResponseDTO;

public interface UserService {
    UserResponseDTO createUser(UserRequestDTO userDto);
}
