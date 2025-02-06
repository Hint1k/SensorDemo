package com.test.sensor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Measurement {
    private Long id;
    private Double temperature;
    private Boolean rain;
    private Sensor sensor;
}