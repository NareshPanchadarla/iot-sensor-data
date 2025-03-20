package com.relay42.iot.sensor.data.security;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.relay42.iot.sensor.data.util.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.servlet.HandlerExceptionResolver;

@ExtendWith(MockitoExtension.class)
class JwtRequestFilterTest {

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private HandlerExceptionResolver handlerExceptionResolver;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private JwtRequestFilter jwtRequestFilter;

    private final String jwtToken = "validToken";
    private final String username = "testUser";

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_ValidToken_SetsAuthentication() throws Exception {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);
        when(jwtTokenUtil.getUsernameFromToken(jwtToken)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtTokenUtil.validateToken(jwtToken, userDetails)).thenReturn(true);
        when(userDetails.getAuthorities()).thenReturn(new java.util.ArrayList<>());

        // Act
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_NoToken_SkipsAuthentication() throws Exception {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_InvalidToken_ThrowsIllegalArgumentException() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);
        when(jwtTokenUtil.getUsernameFromToken(jwtToken)).thenThrow(new IllegalArgumentException("Unable to get JWT token"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                jwtRequestFilter.doFilterInternal(request, response, filterChain));

        assertEquals("Unable to get JWT token", exception.getMessage());
    }

    @Test
    void doFilterInternal_ExpiredToken_ThrowsExpiredJwtException() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);
        when(jwtTokenUtil.getUsernameFromToken(jwtToken)).thenThrow(new ExpiredJwtException(null, null, "Jwt token has expired"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                jwtRequestFilter.doFilterInternal(request, response, filterChain));

        assertEquals("Jwt token has expired", exception.getMessage());
    }

    @Test
    void doFilterInternal_ExceptionInFilterChain_CallsHandlerExceptionResolver() throws Exception {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);
        when(jwtTokenUtil.getUsernameFromToken(jwtToken)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtTokenUtil.validateToken(jwtToken, userDetails)).thenReturn(true);
        doThrow(new RuntimeException("Filter chain exception")).when(filterChain).doFilter(request, response);

        // Act
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(handlerExceptionResolver, times(1)).resolveException(eq(request), eq(response), isNull(), any(RuntimeException.class));
    }
}
