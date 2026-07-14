package com.vaultops.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class LocationCapacityExceededException extends RuntimeException {
    public LocationCapacityExceededException(String message) {
        super(message);
    }
}
