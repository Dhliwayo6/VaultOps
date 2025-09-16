package com.vaultops.exceptions;

import com.vaultops.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AssetNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleAssetNotFoundException(AssetNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(NoResultsException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ErrorResponse handleNoResultsException(NoResultsException e) {
        return new ErrorResponse(e.getMessage());
    }
}
