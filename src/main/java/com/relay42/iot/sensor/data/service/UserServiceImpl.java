package com.relay42.iot.sensor.data.service;

import com.relay42.iot.sensor.data.dto.UserRequestDTO;
import com.relay42.iot.sensor.data.dto.UserResponseDTO;
import com.relay42.iot.sensor.data.entity.User;
import com.relay42.iot.sensor.data.exception.GeneralServerException;
import com.relay42.iot.sensor.data.exception.UserExistsException;
import com.relay42.iot.sensor.data.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder bcryptEncode;

    @Override
    @Transactional
    public UserResponseDTO createUser(UserRequestDTO userDto) {
        try {
            if (userRepository.existsByEmail(userDto.getEmail())) {
                throw new UserExistsException("User is already registered with email:" + userDto.getEmail());
            }
            User newUser = new User();
            BeanUtils.copyProperties(userDto, newUser);
            newUser.setPassword(bcryptEncode.encode(userDto.getPassword()));
            User user = userRepository.save(newUser);
            return new UserResponseDTO(user.getUsername(), user.getEmail());
        } catch (UserExistsException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new GeneralServerException("An unexpected error occurred: " + ex);
        }
    }
}
