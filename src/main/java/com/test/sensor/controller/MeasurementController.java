package com.test.sensor.controller;

import com.test.sensor.entity.Measurement;
import com.test.sensor.service.MeasurementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/measurements")
@Slf4j
public class MeasurementController {

    private final MeasurementService measurementService;

    @Autowired
    public MeasurementController(MeasurementService measurementService) {
        this.measurementService = measurementService;
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addMeasurement(@RequestBody @Valid Measurement measurement) {
        Map<String, String> response = new HashMap<>();
        try {
            Measurement newMeasurement = measurementService.addMeasurement(
                    measurement.getTemperature(), measurement.getRain(), measurement.getSensor().getName()
            );
            log.info("Measurement added: {}", newMeasurement);

            response.put("status", "success");
            response.put("message", "Measurement added successfully");
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<Object> getAllMeasurements() {
        return ResponseEntity.ok(measurementService.getAllMeasurements());
    }

    @GetMapping("/rainyDaysCount")
    public ResponseEntity<Long> getRainyDaysCount() {
        return ResponseEntity.ok(measurementService.countRainyDays());
    }
}