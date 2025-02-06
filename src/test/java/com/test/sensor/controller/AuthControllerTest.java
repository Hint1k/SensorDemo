package com.test.sensor.controller;

import com.test.sensor.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtService jwtService;

    private String username;
    private String password;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        username = "testUser";
        password = "testPassword";

        // Set up authentication object
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        authentication = new UsernamePasswordAuthenticationToken(username, password, authorities);

        // Set security context
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void testLogin_Success() throws Exception {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        // Mock the JwtService to return a fake token
        when(jwtService.generateToken(any(), any())).thenReturn("mocked-jwt-token");

        // Perform POST request to login
        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .param("username", username)
                        .param("password", password)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())  // Expecting HTTP status 200 OK
                .andExpect(content().json("{\"token\":\"mocked-jwt-token\"}"));  // Expecting the mocked token in response

        // Verify that authenticationManager.authenticate was called once
        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        // Verify that jwtService.generateToken was called with the correct parameters
        verify(jwtService, times(1))
                .generateToken(username, authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()));

    }

    // Test the failed login scenario (authentication failure)
    @Test
    void testLogin_Failure() throws Exception {
        // Mock the authenticationManager to throw an exception on failed authentication
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Authentication failed"));

        // Perform POST request to login with incorrect credentials
        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .param("username", username)
                        .param("password", password)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isUnauthorized());  // Expecting HTTP status 401 Unauthorized

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        // Since authentication failed, jwtService.generateToken should not be called
        verify(jwtService, never()).generateToken(any(), any());
    }
}