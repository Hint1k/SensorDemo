package com.test.sensor.service;

import com.test.sensor.entity.Measurement;
import com.test.sensor.entity.Sensor;
import com.test.sensor.repository.MeasurementRepository;
import com.test.sensor.repository.SensorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class MeasurementServiceImplTest {

    @Mock
    private MeasurementRepository measurementRepository;

    @Mock
    private SensorRepository sensorRepository;

    @InjectMocks
    private MeasurementServiceImpl measurementServiceImpl;

    @Test
    void testAddMeasurement() {
        Sensor sensor = new Sensor();
        sensor.setName("TestSensor");

        when(sensorRepository.findByName("TestSensor")).thenReturn(Optional.of(sensor));
        when(measurementRepository.save(any(Measurement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Measurement measurement = measurementServiceImpl
                .addMeasurement(25.5, true, "TestSensor");

        assertThat(measurement).isNotNull();
        assertThat(measurement.getTemperature()).isEqualTo(25.5);
        assertThat(measurement.getRain()).isTrue();
        assertThat(measurement.getSensor().getName()).isEqualTo("TestSensor");

        verify(sensorRepository, times(1)).findByName("TestSensor");
        verify(measurementRepository, times(1)).save(any(Measurement.class));
    }

    @Test
    void testCountRainyDays() {
        when(measurementRepository.countByRainTrue()).thenReturn(5L);

        long rainyDaysCount = measurementServiceImpl.countRainyDays();

        assertThat(rainyDaysCount).isEqualTo(5L);

        verify(measurementRepository, times(1)).countByRainTrue();
    }

    @Test
    void testGetAllMeasurements() {
        Measurement measurement = new Measurement();
        when(measurementRepository.findAll()).thenReturn(List.of(measurement));

        List<Measurement> measurements = measurementServiceImpl.getAllMeasurements();

        assertThat(measurements).hasSize(1);
        verify(measurementRepository, times(1)).findAll();
    }
}