package com.vaultops.exceptions;

import com.vaultops.enums.ErrorMessages;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.OK)
public class NoResultsException extends RuntimeException {
    public NoResultsException() {
        super(ErrorMessages.NO_RESULTS.getMessage());
    }
}
