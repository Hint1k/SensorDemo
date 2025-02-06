package com.test.sensor.service;

import com.test.sensor.entity.Sensor;
import com.test.sensor.repository.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SensorServiceImpl implements SensorService {

    private final SensorRepository sensorRepository;

    @Autowired
    public SensorServiceImpl(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    @Override
    @Transactional
    public Sensor registerSensor(String name) {

        Optional<Sensor> existingSensor = sensorRepository.findByName(name);
        if (existingSensor.isPresent()) {
            throw new IllegalArgumentException("Sensor with this name already exists");
        }

        Sensor sensor = new Sensor();
        sensor.setName(name);
        sensorRepository.save(sensor);
        return sensor;
    }
}