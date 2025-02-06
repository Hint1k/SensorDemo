package com.test.sensor.repository;

import com.test.sensor.entity.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeasurementRepository extends JpaRepository<Measurement, Long> {

    List<Measurement> findAll();

    long countByRainTrue();
}