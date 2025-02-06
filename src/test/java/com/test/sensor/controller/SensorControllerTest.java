package com.test.sensor.controller;

import com.test.sensor.entity.Sensor;
import com.test.sensor.jwt.JwtService;
import com.test.sensor.service.SensorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SensorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SensorService sensorService;

    @Autowired
    private JwtService jwtService;

    // Define the JSON strings as constants
    private static final String VALID_SENSOR_JSON = "{\"name\":\"TestSensor\"}";
    private static final String SUCCESS_RESPONSE_JSON =
            "{\"status\":\"success\",\"message\":\"Sensor registered successfully\"}";
    private static final String ERROR_RESPONSE_JSON =
            "{\"status\":\"error\",\"message\":\"Sensor with this name already exists\"}";

    private String token;

    @BeforeEach
    void setUp() {
        // Generate a valid JWT token for the "admin" user with the "ROLE_ADMIN" authority.
        token = jwtService.generateToken("admin", List.of("ROLE_ADMIN"));
    }

    @Test
    void testRegisterSensor_Success() throws Exception {
        Sensor sensor = new Sensor();
        sensor.setId(1L);
        sensor.setName("TestSensor");

        when(sensorService.registerSensor(any(String.class))).thenReturn(sensor);

        mockMvc.perform(post("/sensors/registration")
                        .with(csrf())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_SENSOR_JSON))
                .andExpect(status().isCreated())  // Expecting HTTP status 201 Created
                .andExpect(content().json(SUCCESS_RESPONSE_JSON));

        // Verify that the service method was called exactly once
        verify(sensorService, times(1)).registerSensor("TestSensor");
    }

    @Test
    void testRegisterSensor_AlreadyExists() throws Exception {
        doThrow(new IllegalArgumentException("Sensor with this name already exists"))
                .when(sensorService).registerSensor(any(String.class));

        mockMvc.perform(post("/sensors/registration")
                        .with(csrf())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_SENSOR_JSON))
                .andExpect(status().isBadRequest())  // Expecting HTTP status 400 Bad Request
                .andExpect(content().json(ERROR_RESPONSE_JSON));

        // Verify that the service method was called exactly once
        verify(sensorService, times(1)).registerSensor("TestSensor");
    }
}