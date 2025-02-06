package com.test.sensor.controller;

import com.test.sensor.entity.Measurement;
import com.test.sensor.jwt.JwtService;
import com.test.sensor.service.MeasurementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class MeasurementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MeasurementService measurementService;

    @Autowired
    private JwtService jwtService;

    // Define the JSON strings as constants
    private static final String VALID_MEASUREMENT_JSON =
            "{\"temperature\":25.50,\"rain\":true,\"sensor\":{\"name\":\"TestSensor\"}}";
    private static final String SUCCESS_RESPONSE_JSON =
            "{\"status\":\"success\",\"message\":\"Measurement added successfully\"}";
    private static final String ERROR_RESPONSE_JSON =
            "{\"status\":\"error\",\"message\":\"Invalid measurement data\"}";

    private String token;

    @BeforeEach
    void setUp() {
        // Generate a valid JWT token for the "admin" user with the "ROLE_ADMIN" authority.
        token = jwtService.generateToken("admin", List.of("ROLE_ADMIN"));
    }

    @Test
    void testAddMeasurement_Success() throws Exception {
        // Creating a mock measurement object
        Measurement measurement = new Measurement();
        measurement.setTemperature(25.50);
        measurement.setRain(true);

        // Mocking the service method to return the above measurement
        when(measurementService.addMeasurement(any(Double.class), any(Boolean.class), any(String.class)))
                .thenReturn(measurement);

        // Perform POST request to add measurement
        mockMvc.perform(post("/measurements/add")
                        .with(csrf())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_MEASUREMENT_JSON))
                .andExpect(status().isCreated())  // Expecting HTTP status 201 Created
                .andExpect(content().json(SUCCESS_RESPONSE_JSON));

        // Verify that the addMeasurement method was called exactly once
        verify(measurementService, times(1))
                .addMeasurement(25.50, true, "TestSensor");
    }

    @Test
    void testAddMeasurement_InvalidInput() throws Exception {
        // Simulating an error from the service layer (IllegalArgumentException)
        doThrow(new IllegalArgumentException("Invalid measurement data"))
                .when(measurementService).addMeasurement(any(Double.class), any(Boolean.class), any(String.class));

        // Perform POST request with invalid input
        mockMvc.perform(post("/measurements/add")
                        .with(csrf())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_MEASUREMENT_JSON))
                .andExpect(status().isBadRequest())  // Expecting HTTP status 400 Bad Request
                .andExpect(content().json(ERROR_RESPONSE_JSON));

        // Verify that the addMeasurement method was called exactly once
        verify(measurementService, times(1))
                .addMeasurement(25.50, true, "TestSensor");
    }

    @Test
    void testGetAllMeasurements() throws Exception {
        // Mocking the service to return an empty list of measurements
        when(measurementService.getAllMeasurements()).thenReturn(new ArrayList<>());

        // Perform GET request to retrieve all measurements
        mockMvc.perform(get("/measurements")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())  // Expecting HTTP status 200 OK
                .andExpect(content().json("[]"));  // Expecting empty list as response

        // Verify that the getAllMeasurements method was called exactly once
        verify(measurementService, times(1)).getAllMeasurements();
    }

    @Test
    void testGetRainyDaysCount() throws Exception {
        // Mocking the service to return count of rainy days (5 in this case)
        when(measurementService.countRainyDays()).thenReturn(5L);

        // Perform GET request to retrieve count of rainy days
        mockMvc.perform(get("/measurements/rainyDaysCount")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())  // Expecting HTTP status 200 OK
                .andExpect(content().json("5"));  // Expecting "5" as the number of rainy days

        // Verify that the countRainyDays method was called exactly once
        verify(measurementService, times(1)).countRainyDays();
    }
}