package com.test.sensor;

import com.test.sensor.controller.MeasurementController;
import com.test.sensor.service.SensorServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SensorApplicationTests {

	private final MeasurementController measurementController;
	private final SensorServiceImpl sensorServiceImpl;

	@Autowired
    SensorApplicationTests(MeasurementController measurementController1, SensorServiceImpl sensorServiceImpl) {
        this.measurementController = measurementController1;
        this.sensorServiceImpl = sensorServiceImpl;
    }

    @Test // testing if the test would even work
	void contextLoads() {
		Assertions.assertNotNull(measurementController);
		Assertions.assertNotNull(sensorServiceImpl);
	}
}