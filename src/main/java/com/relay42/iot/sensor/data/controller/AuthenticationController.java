package com.relay42.iot.sensor.data.controller;

import com.relay42.iot.sensor.data.dto.AuthDTO;
import com.relay42.iot.sensor.data.dto.JwtResponseDTO;
import com.relay42.iot.sensor.data.dto.UserRequestDTO;
import com.relay42.iot.sensor.data.dto.UserResponseDTO;

import com.relay42.iot.sensor.data.security.AuthenticateService;
import com.relay42.iot.sensor.data.security.CustomUserDetailsService;
import com.relay42.iot.sensor.data.service.UserService;
import com.relay42.iot.sensor.data.util.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sensors")
@RequiredArgsConstructor
@Tag(name = "Authentication Controller", description = "APIs for authentication")
public class AuthenticationController {
    private final AuthenticateService authenticateService;
    private final UserService userService;
    private final CustomUserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;

    @Operation(summary = "User Login", description = "Authenticate a user and receive a JWT token.")
    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> login(@RequestBody AuthDTO authDto) throws Exception {
        authenticateService.authenticate(authDto.getEmail(), authDto.getPassword());
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authDto.getEmail());
        final String token = jwtTokenUtil.generateToken(userDetails.getUsername());
        return new ResponseEntity<>(new JwtResponseDTO(token), HttpStatus.OK);
    }

    @Operation(summary = "User Registration", description = "Register a new user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully registered",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> create(@Valid @RequestBody UserRequestDTO userRequestDto) {
        return new ResponseEntity<>(userService.createUser(userRequestDto), HttpStatus.CREATED);
    }
}
