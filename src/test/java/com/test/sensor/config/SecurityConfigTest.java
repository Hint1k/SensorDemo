package com.test.sensor.config;

import com.test.sensor.entity.Sensor;
import com.test.sensor.jwt.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private AuthenticationManager authenticationManager;

    private static final String SENSOR = "{\"id\":null,\"name\":\"TemperatureSensor\"}";
    private static final String REGISTERED = "{\"status\":\"success\",\"message\":\"Sensor registered successfully\"}";

    @Test
    void testLoginPublicAccess() throws Exception {
        // Mock the authentication manager to avoid NullPointerException
        when(authenticationManager.authenticate(any())).thenReturn(
                new UsernamePasswordAuthenticationToken("user", "password",
                        List.of(new SimpleGrantedAuthority("ROLE_USER")))
        );

        mockMvc.perform(post("/auth/login")
                        .param("username", "user")
                        .param("password", "password"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().isFound())  // 302 - Redirection
                .andExpect(header().string("Location", "/swagger-ui/index.html"));

        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/swagger-ui/index.html"));

        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }

    @Test
    public void testRoleBasedAccessForAdmin() throws Exception {
        // Generate a valid JWT token for the "admin" user with the "ROLE_ADMIN" authority.
        String token = jwtService.generateToken("admin", List.of("ROLE_ADMIN"));

        // Test accessing the /measurements endpoint with the "admin" user's token.
        mockMvc.perform(get("/measurements")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // Test accessing another protected endpoint, /measurements/rainyDaysCount, using the same token.
        mockMvc.perform(get("/measurements/rainyDaysCount")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // Create a new sensor object to test sensor registration.
        Sensor sensor = new Sensor();
        sensor.setName("TemperatureSensor");

        // Test posting a new sensor registration with the "admin" user's token.
        mockMvc.perform(post("/sensors/registration")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(SENSOR))
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(content().json(REGISTERED));
    }

    @Test
    public void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(post("/measurements/add")
                        .contentType("application/json")
                        .content("{}")) // empty JSON for the required body
                .andExpect(status().isUnauthorized());  // Expects 401 lack of authorization

        mockMvc.perform(post("/sensors/registration")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }
}