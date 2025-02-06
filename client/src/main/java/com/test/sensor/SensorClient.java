package com.test.sensor;

import com.test.sensor.dto.Measurement;
import com.test.sensor.dto.ResponseMessage;
import com.test.sensor.dto.Sensor;
import com.test.sensor.dto.TokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.Random;

@Slf4j
// This class and the whole module imitates the request made from some other service to the SensorApplication
public class SensorClient {

    private static final String BASE_URL = "http://localhost:8080";
    private static final String SENSOR_REGISTRATION_URL = BASE_URL + "/sensors/registration";
    private static final String MEASUREMENTS_URL = BASE_URL + "/measurements/add";
    private static final String GET_MEASUREMENTS_URL = BASE_URL + "/measurements";
    private static final String LOGIN_URL = BASE_URL + "/auth/login";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "123";
    private static final int MEASUREMENTS_QUANTITY = 1000;

    private String jwtToken;

    private final RestTemplate restTemplate = new RestTemplate();
    private final Random random = new Random();

    public static void main(String[] args) {
        SensorClient client = new SensorClient();
        String token = client.login();
        if (token != null) {
            client.run();
        } else {
            log.error("Login failed. Cannot proceed without a valid token.");
            System.exit(1);  // Exit the program with a non-zero status to indicate failure
        }
    }

    private void run() {
        Sensor sensor = registerSensor();
        sendMeasurements(sensor); // Pass the entire sensor object
        getMeasurements();
    }

    private String login() {
        String loginUri = String.format("%s?username=%s&password=%s", LOGIN_URL, USERNAME, PASSWORD);
        ResponseEntity<TokenResponse> response =
                makeRestCall(loginUri, HttpMethod.POST, null, TokenResponse.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            jwtToken = Objects.requireNonNull(response.getBody()).getToken();
            log.info("Token received: {}", jwtToken);
            return jwtToken;
        } else {
            log.info("Login failed, status code: {}", response.getStatusCode());
            return null;
        }
    }

    private Sensor registerSensor() {
        Sensor sensor = new Sensor();
        sensor.setName("Sensor-" + random.nextInt(1000));  // Random sensor name
        HttpEntity<Sensor> requestEntity = createRequestEntity(sensor);

        ResponseEntity<ResponseMessage> response =
                makeRestCall(SENSOR_REGISTRATION_URL, HttpMethod.POST, requestEntity, ResponseMessage.class);

        if (response.getStatusCode() == HttpStatus.CREATED) {
            log.info("Sensor registered successfully: {}", Objects.requireNonNull(response.getBody()).getMessage());
            return sensor;
        } else {
            log.error("Failed to register sensor: {}", Objects.requireNonNull(response.getBody()).getMessage());
            throw new RuntimeException("Failed to register sensor");
        }
    }

    private void sendMeasurements(Sensor sensor) {
        for (int i = 0; i < MEASUREMENTS_QUANTITY; i++) {
            Measurement measurement = createMeasurement(sensor, i + 1);
            HttpEntity<Measurement> requestEntity = createRequestEntity(measurement);

            ResponseEntity<ResponseMessage> response =
                    makeRestCall(MEASUREMENTS_URL, HttpMethod.POST, requestEntity, ResponseMessage.class);
            logMeasurementStatus(response, i + 1);
        }
    }

    private void getMeasurements() {
        HttpHeaders headers = createHeaders();
        HttpEntity<Void> entity = new HttpEntity<>(headers);  // Empty body for GET request
        ResponseEntity<Measurement[]> response =
                makeRestCall(GET_MEASUREMENTS_URL, HttpMethod.GET, entity, Measurement[].class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Measurement[] measurements = response.getBody();
            log.info("Retrieved measurements:");
            for (Measurement measurement : Objects.requireNonNull(measurements)) {
                log.info("{}", measurement);
            }
        } else {
            log.info("Failed to retrieve measurements: {}", response.getStatusCode());
        }
    }

    // Create headers for authentication
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    // Create a request entity with headers and body
    private <T> HttpEntity<T> createRequestEntity(T body) {
        HttpHeaders headers = createHeaders();
        return new HttpEntity<>(body, headers);
    }

    // Create a measurement with random data and sensor
    private Measurement createMeasurement(Sensor sensor, int index) {
        Measurement measurement = new Measurement();
        double temperature = -40.00 + (random.nextDouble() * 80.00);
        measurement.setTemperature(Double.parseDouble(String.format("%.2f", temperature)));
        measurement.setRain(random.nextBoolean());  // Random rain value (true or false)
        measurement.setSensor(sensor);  // Set the full sensor object
        return measurement;
    }

    // Perform REST call (GET/POST)
    private <T> ResponseEntity<T> makeRestCall(
            String url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType
    ) {
        return restTemplate.exchange(url, method, requestEntity, responseType);
    }

    // Log the status of a measurement operation
    private void logMeasurementStatus(ResponseEntity<ResponseMessage> response, int index) {
        if (response.getStatusCode() == HttpStatus.CREATED) {
            log.info("Measurement {} added: {}", index, Objects.requireNonNull(response.getBody()).getMessage());
        } else {
            log.info("Failed to add measurement {}: {}", index,
                    Objects.requireNonNull(response.getBody()).getMessage());
        }
    }
}