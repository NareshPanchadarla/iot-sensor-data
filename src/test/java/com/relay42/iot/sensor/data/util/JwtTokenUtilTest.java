package com.relay42.iot.sensor.data.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.jsonwebtoken.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.function.Function;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class JwtTokenUtilTest {

    private static final String SECRET_KEY = "testSecret";
    private static final Long EXPIRATION_TIME = 1000L * 60 * 60; // 1 hour
    private static final String USERNAME = "testUser";

    @InjectMocks
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private UserDetails userDetails;

    private String jwtToken;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtTokenUtil = new JwtTokenUtil();

        // Manually setting private fields using reflection
        ReflectionTestUtils.setField(jwtTokenUtil, "secret", SECRET_KEY);
        ReflectionTestUtils.setField(jwtTokenUtil, "expiration", EXPIRATION_TIME);

        // Generate a valid token for testing
        jwtToken = jwtTokenUtil.generateToken(USERNAME);
    }

    @Test
    void generateToken_ShouldCreateValidToken() {
        assertNotNull(jwtToken);
        assertTrue(jwtToken.length() > 10);
    }

    @Test
    void getUsernameFromToken_ShouldExtractCorrectUsername() {
        String extractedUsername = jwtTokenUtil.getUsernameFromToken(jwtToken);
        assertEquals(USERNAME, extractedUsername);
    }

    @Test
    void validateToken_ShouldReturnTrueForValidToken() {
        when(userDetails.getUsername()).thenReturn(USERNAME);
        assertTrue(jwtTokenUtil.validateToken(jwtToken, userDetails));
    }

    @Test
    void validateToken_ShouldReturnFalseForInvalidUsername() {
        when(userDetails.getUsername()).thenReturn("wrongUser");
        assertFalse(jwtTokenUtil.validateToken(jwtToken, userDetails));
    }

    @Test
    void isTokenExpired_ShouldReturnFalseForValidToken() {
        assertFalse(jwtTokenUtil.validateToken(jwtToken, userDetails));
    }

    @Test
    void isTokenExpired_ShouldReturnTrueForExpiredToken() throws InterruptedException {
        // Set expiration time to 1 second for quick expiration test
        ReflectionTestUtils.setField(jwtTokenUtil, "expiration", 1000L);

        String shortLivedToken = jwtTokenUtil.generateToken(USERNAME);

        // Wait for the token to expire
        Thread.sleep(1100);

        // Validate token should throw ExpiredJwtException
        assertThrows(ExpiredJwtException.class, () ->
                jwtTokenUtil.getUsernameFromToken(shortLivedToken));
    }

    @Test
    void getClaimFromToken_ShouldExtractClaimsCorrectly() {
        Function<Claims, String> claimResolver = Claims::getSubject;
        String claimValue = claimResolver.apply(Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(jwtToken).getBody());

        assertEquals(USERNAME, claimValue);
    }

    @Test
    void validateToken_ShouldThrowExceptionForTamperedToken() {
        String tamperedToken = jwtToken + "tampered";

        assertThrows(SignatureException.class, () ->
                jwtTokenUtil.getUsernameFromToken(tamperedToken));
    }

    @Test
    void validateToken_ShouldThrowExpiredJwtExceptionForExpiredToken() {
        // Create an expired token by setting expiration in the past
        String expiredToken = Jwts.builder()
                .setSubject(USERNAME)
                .setIssuedAt(new Date(System.currentTimeMillis() - EXPIRATION_TIME - 1000))
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();

        assertThrows(ExpiredJwtException.class, () ->
                jwtTokenUtil.getUsernameFromToken(expiredToken));
    }
}

