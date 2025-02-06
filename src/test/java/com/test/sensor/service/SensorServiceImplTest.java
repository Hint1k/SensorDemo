package com.test.sensor.service;

import com.test.sensor.entity.Sensor;
import com.test.sensor.repository.SensorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class SensorServiceImplTest {

    @Mock
    private SensorRepository sensorRepository;

    @InjectMocks
    private SensorServiceImpl sensorServiceImpl;

    @Test
    void testRegisterSensor_Success() {
        String sensorName = "TestSensor";

        // Simulating that no sensor exists with this name
        when(sensorRepository.findByName(sensorName)).thenReturn(Optional.empty());
        when(sensorRepository.save(any(Sensor.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Sensor sensor = sensorServiceImpl.registerSensor(sensorName);

        assertThat(sensor).isNotNull();
        assertThat(sensor.getName()).isEqualTo(sensorName);

        verify(sensorRepository, times(1)).findByName(sensorName);
        verify(sensorRepository, times(1)).save(any(Sensor.class));
    }

    @Test
    void testRegisterSensor_SensorAlreadyExists() {
        String sensorName = "TestSensor";

        // Simulating that a sensor with the same name already exists
        Sensor existingSensor = new Sensor();
        existingSensor.setName(sensorName);
        when(sensorRepository.findByName(sensorName)).thenReturn(Optional.of(existingSensor));

        // Expecting an IllegalArgumentException when trying to register the same sensor
        try {
            sensorServiceImpl.registerSensor(sensorName);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).isEqualTo("Sensor with this name already exists");
        }

        verify(sensorRepository, times(1)).findByName(sensorName);
        verify(sensorRepository, times(0)).save(any(Sensor.class)); // save should not be called
    }
}