package com.test.sensor.service;

import com.test.sensor.entity.Measurement;
import com.test.sensor.entity.Sensor;
import com.test.sensor.repository.MeasurementRepository;
import com.test.sensor.repository.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MeasurementServiceImpl implements MeasurementService {

    private  final MeasurementRepository measurementRepository;
    private final SensorRepository sensorRepository;

    @Autowired
    public MeasurementServiceImpl(MeasurementRepository measurementRepository, SensorRepository sensorRepository) {
        this.measurementRepository = measurementRepository;
        this.sensorRepository = sensorRepository;
    }

    @Override
    @Transactional
    public Measurement addMeasurement(double temperature, boolean rain, String sensorName) {
        Sensor sensor = sensorRepository.findByName(sensorName)
                .orElseThrow(() -> new IllegalArgumentException("Sensor not found"));

        Measurement measurement = new Measurement();
        measurement.setTemperature(temperature);
        measurement.setRain(rain);
        measurement.setSensor(sensor);

        measurementRepository.save(measurement);
        return measurement;
    }

    @Override
    @Transactional
    public long countRainyDays() {
        return measurementRepository.countByRainTrue();
    }

    @Override
    @Transactional
    public List<Measurement> getAllMeasurements() {
        return measurementRepository.findAll();
    }
}