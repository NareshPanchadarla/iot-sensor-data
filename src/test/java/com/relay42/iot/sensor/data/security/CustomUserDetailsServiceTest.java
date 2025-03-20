package com.relay42.iot.sensor.data.security;

import com.relay42.iot.sensor.data.entity.User;
import com.relay42.iot.sensor.data.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)  // Enable Mockito in JUnit 5
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;  // Mock UserRepository

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;  // Inject mock

    private final String email = "test@example.com";

    @Test
    void loadUserByUsername_UserExists_ShouldReturnUserDetails() {
        // Arrange: Mock the user repository to return a valid user
        User mockUser = new User();
        mockUser.setEmail(email);
        String password = "password123";
        mockUser.setPassword(password);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        // Assert
        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());
    }

    @Test
    void loadUserByUsername_UserNotFound_ShouldThrowUsernameNotFoundException() {
        // Arrange: Mock repository to return empty (user not found)
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert: Expect exception when user is not found
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () ->
                customUserDetailsService.loadUserByUsername(email));

        assertEquals("User not found for the email:" + email, exception.getMessage());
    }
}
