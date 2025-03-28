package com.relay42.iot.sensor.data.exception;

import java.io.Serial;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class GeneralServerException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public GeneralServerException(String message) {
        super(message);
    }
}
