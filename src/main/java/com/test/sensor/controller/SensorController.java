package com.test.sensor.controller;

import com.test.sensor.dto.ResponseMessage;
import com.test.sensor.entity.Sensor;
import com.test.sensor.service.SensorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.http.*;

@RestController
@RequestMapping("/sensors")
@Slf4j
public class SensorController {

    private final SensorService sensorService;

    @Autowired
    public SensorController(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    @PostMapping("/registration")
    public ResponseEntity<ResponseMessage> registerSensor(@RequestBody @Valid Sensor sensor) {
        ResponseMessage responseMessage = new ResponseMessage();

        try {
            // Call the service to register the sensor
            Sensor newSensor = sensorService.registerSensor(sensor.getName());
            log.info("Sensor registered: {}", newSensor);

            // Set success response message
            responseMessage.setStatus("success");
            responseMessage.setMessage("Sensor registered successfully");

            // Return 201 Created status code
            return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage);
        } catch (IllegalArgumentException e) {
            // Set error response message
            responseMessage.setStatus("error");
            responseMessage.setMessage(e.getMessage());

            return ResponseEntity.badRequest().body(responseMessage);
        }
    }
}