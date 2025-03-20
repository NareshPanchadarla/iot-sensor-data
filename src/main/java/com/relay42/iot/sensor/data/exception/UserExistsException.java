package com.relay42.iot.sensor.data.exception;

import java.io.Serial;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class UserExistsException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public UserExistsException(String message) {
        super(message);
    }
}
