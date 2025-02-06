package com.test.sensor.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResponseMessage {
    private String status;
    private String message;
}