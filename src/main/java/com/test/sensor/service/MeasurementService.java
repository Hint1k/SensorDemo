package com.test.sensor.service;

import com.test.sensor.entity.Measurement;

import java.util.List;

public interface MeasurementService {

    Measurement addMeasurement(double temperature, boolean rain, String sensorName);

    long countRainyDays();

    List<Measurement> getAllMeasurements();
}