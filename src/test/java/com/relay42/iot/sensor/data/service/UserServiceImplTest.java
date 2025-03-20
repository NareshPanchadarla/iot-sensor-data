package com.relay42.iot.sensor.data.service;

import com.relay42.iot.sensor.data.dto.UserRequestDTO;
import com.relay42.iot.sensor.data.dto.UserResponseDTO;
import com.relay42.iot.sensor.data.entity.User;
import com.relay42.iot.sensor.data.exception.GeneralServerException;
import com.relay42.iot.sensor.data.exception.UserExistsException;
import com.relay42.iot.sensor.data.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bcryptEncode;

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRequestDTO userRequestDto;

    @Mock
    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateUser_UserExists() {
        // Arrange
        String email = "test@example.com";
        String username = "tester";
        when(userRequestDto.getEmail()).thenReturn(email);
        when(userRequestDto.getUsername()).thenReturn(username);
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // Act & Assert
        UserExistsException exception = assertThrows(UserExistsException.class, () -> userService.createUser(userRequestDto));

        assertEquals("User is already registered with email:test@example.com", exception.getMessage());
    }

    @Test
    public void testCreateUser_Success() {
        // Arrange
        String email = "test@example.com";
        String username = "tester";
        String password = "password123";
        UserRequestDTO userRequestDto = new UserRequestDTO(username, email, password);

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(bcryptEncode.encode(password)).thenReturn("encodedPassword");

        // Simulate User creation
        when(user.getUsername()).thenReturn(username);
        when(user.getEmail()).thenReturn(email);

        // Act
        UserResponseDTO response = userService.createUser(userRequestDto);

        // Assert
        assertNotNull(response);
        assertEquals(username, response.getUsername());
        assertEquals(email, response.getEmail());
        verify(userRepository, times(1)).save(any(User.class));  // Verify save method was called once
    }

    @Test
    public void testCreateUser_UnhandledException() {
        // Arrange
        String email = "test@example.com";
        String username = "tester";
        String password = "password123";
        UserRequestDTO userRequestDto = new UserRequestDTO(username, email, password);

        when(userRepository.existsByEmail(email)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        GeneralServerException exception = assertThrows(GeneralServerException.class, () -> userService.createUser(userRequestDto));

        assertEquals("An unexpected error occurred: java.lang.RuntimeException: Database error", exception.getMessage());
    }
}
