package com.relay42.iot.sensor.data.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.relay42.iot.sensor.data.config.CustomTestSecurityConfig;
import com.relay42.iot.sensor.data.config.CustomUserDetails;
import com.relay42.iot.sensor.data.dto.AuthDTO;
import com.relay42.iot.sensor.data.dto.UserRequestDTO;
import com.relay42.iot.sensor.data.dto.UserResponseDTO;
import com.relay42.iot.sensor.data.entity.User;
import com.relay42.iot.sensor.data.repository.UserRepository;
import com.relay42.iot.sensor.data.security.AuthenticateService;
import com.relay42.iot.sensor.data.security.CustomUserDetailsService;
import com.relay42.iot.sensor.data.service.UserService;
import com.relay42.iot.sensor.data.util.JwtTokenUtil;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(CustomTestSecurityConfig.class)
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticateService authenticateService;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    public void testCreateUser() throws Exception {
        // Arrange: Prepare the input and expected output
        UserRequestDTO userRequestDto = new UserRequestDTO("john_doe", "john.doe@example.com", "password123");
        UserResponseDTO userResponseDto = new UserResponseDTO("john_doe", "john.doe@example.com");

        // Mock the behavior of the UserService
        when(userService.createUser(any(UserRequestDTO.class))).thenReturn(userResponseDto);

        // Convert the request DTO to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String userRequestJson = objectMapper.writeValueAsString(userRequestDto);

        // Act: Perform the POST request
        mockMvc.perform(post("/api/v1/sensors/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userRequestJson)
                        .characterEncoding("utf-8"))
                // Assert: Verify the response
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("john_doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }


    @Test
    public void testLogin_Success() throws Exception {
        // Arrange: Prepare the input and expected output
        AuthDTO authDto = new AuthDTO("user@example.com", "password123");
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        UserDetails customUserDetails = new CustomUserDetails("user@example.com", "password123", true, authorities);

        String token = "eyJhbGciOiJIUzUxMiJ9";

        User user = new User();
        user.setUsername("test_user");
        user.setEmail("user@example.com");

        // Mock the behavior of the dependencies
        doNothing().when(authenticateService).authenticate(authDto.getEmail(), authDto.getPassword());
        when(userRepository.findByEmail(authDto.getEmail())).thenReturn(Optional.of(user));
        when(customUserDetailsService.loadUserByUsername(authDto.getEmail())).thenReturn(customUserDetails);
        when(jwtTokenUtil.generateToken(customUserDetails.getUsername())).thenReturn(token);

        // Convert the request DTO to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String authRequestJson = objectMapper.writeValueAsString(authDto);

        // Act: Perform the POST request
        mockMvc.perform(post("/api/v1/sensors/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authRequestJson))
                // Assert: Verify the response
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwtToken").value(token));
    }
}
