package com.relay42.iot.sensor.data.security;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.relay42.iot.sensor.data.exception.GeneralServerException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)  // Enable Mockito in JUnit 5
class AuthenticateServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;  // Mock dependency

    @InjectMocks
    private AuthenticateService authenticateService;  // Inject mock dependencies

    private final String email = "test@example.com";
    private final String password = "password123";

    @Test
    void authenticate_Successful() {
        // Arrange: Simulate successful authentication by returning a mock Authentication object
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));

        // Act & Assert: No exception should be thrown
        assertDoesNotThrow(() -> authenticateService.authenticate(email, password));
    }

    @Test
    void authenticate_UserDisabled_ShouldThrowException() {
        // Arrange: Simulate a DisabledException
        doThrow(new DisabledException("User disabled"))
                .when(authenticationManager)
                .authenticate(any());

        // Act & Assert: Expect an Exception with "User disabled"
        Exception exception = assertThrows(Exception.class, () ->
                authenticateService.authenticate(email, password));
        assertEquals("User disabled", exception.getMessage());
    }

    @Test
    void authenticate_BadCredentials_ShouldThrowGeneralServerException() {
        // Arrange: Simulate a BadCredentialsException
        doThrow(new BadCredentialsException("Bad Credentials"))
                .when(authenticationManager)
                .authenticate(any());

        // Act & Assert: Expect GeneralServerException
        Exception exception = assertThrows(GeneralServerException.class, () ->
                authenticateService.authenticate(email, password));
        assertEquals("Bad Credentials", exception.getMessage());
    }
}
