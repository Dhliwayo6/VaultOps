package com.vaultops.exceptions;

import com.vaultops.model.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AssetNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleAssetNotFoundException(AssetNotFoundException e, HttpServletRequest request) {
        logErrorContext(request, HttpStatus.NOT_FOUND, e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(NoResultsException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ErrorResponse handleNoResultsException(NoResultsException e, HttpServletRequest request) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest request) {
        logErrorContext(request, HttpStatus.BAD_REQUEST, e);
        String message = "Invalid JSON input or malformed enum value";
        if (e.getCause() instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) e.getCause();
            if (ife.getTargetType().isEnum()) {
                message = "Invalid value for enum: " + ife.getValue();
            }
        }
        return new ErrorResponse(message);
    }

    @ExceptionHandler(MigrationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleMigrationNotFoundException(MigrationNotFoundException e, HttpServletRequest request) {
        logErrorContext(request, HttpStatus.NOT_FOUND, e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(MaintenanceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleMaintenanceNotFoundException(MaintenanceNotFoundException e, HttpServletRequest request) {
        logErrorContext(request, HttpStatus.NOT_FOUND, e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(InvalidFileException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleInvalidFileException(InvalidFileException e, HttpServletRequest request) {
        logErrorContext(request, HttpStatus.BAD_REQUEST, e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(LocationCapacityExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleLocationCapacityExceededException(LocationCapacityExceededException e, HttpServletRequest request) {
        logErrorContext(request, HttpStatus.BAD_REQUEST, e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(NoDataException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleNoDataException(NoDataException e, HttpServletRequest request) {
        logErrorContext(request, HttpStatus.NOT_FOUND, e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(ExportException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleExportException(ExportException e, HttpServletRequest request) {
        logErrorContext(request, HttpStatus.INTERNAL_SERVER_ERROR, e);
        return new ErrorResponse(e.getMessage());
    }

    // Spring built-in exception - single argument for unit test compatibility
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    @ResponseBody
    public ErrorResponse handleMaxUploadSizeExceeded(MaxUploadSizeExceededException e) {
        return handleMaxUploadSizeExceeded(e, null);
    }

    public ErrorResponse handleMaxUploadSizeExceeded(MaxUploadSizeExceededException e, HttpServletRequest request) {
        logErrorContext(request, HttpStatus.PAYLOAD_TOO_LARGE, e);
        return new ErrorResponse("File size exceeds maximum allowed size of 10MB");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        logErrorContext(request, HttpStatus.BAD_REQUEST, e);
        return new ErrorResponse("Invalid parameter: " + e.getMessage());
    }

    @ExceptionHandler(JobNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleJobNotFoundException(JobNotFoundException e, HttpServletRequest request) {
        logErrorContext(request, HttpStatus.NOT_FOUND, e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        logErrorContext(request, HttpStatus.BAD_REQUEST, e);
        String message = "Validation failed: " + errors.size() + " error(s)";
        return new ErrorResponse(message, errors);
    }

    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleConstraintViolationException(jakarta.validation.ConstraintViolationException e, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        for (jakarta.validation.ConstraintViolation<?> violation : e.getConstraintViolations()) {
            String path = violation.getPropertyPath().toString();
            int lastDot = path.lastIndexOf('.');
            String fieldName = lastDot != -1 ? path.substring(lastDot + 1) : path;
            errors.put(fieldName, violation.getMessage());
        }
        logErrorContext(request, HttpStatus.BAD_REQUEST, e);
        String message = "Validation failed: " + errors.size() + " error(s)";
        return new ErrorResponse(message, errors);
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ErrorResponse handleAccessDenied(org.springframework.security.access.AccessDeniedException e, HttpServletRequest request) {
        logErrorContext(request, HttpStatus.FORBIDDEN, e);
        return new ErrorResponse("Access Denied: You do not have permission to perform this action.");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        logErrorContext(request, HttpStatus.BAD_REQUEST, e);
        return new ErrorResponse("Invalid parameter: " + e.getName() + " has invalid value or format");
    }

    // Catch-all for unhandled server exceptions (500 Internal Server Error)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e, HttpServletRequest request) throws Exception {
        if (AnnotatedElementUtils.hasAnnotation(e.getClass(), ResponseStatus.class)) {
            throw e;
        }
        logErrorContext(request, HttpStatus.INTERNAL_SERVER_ERROR, e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("An internal server error occurred."));
    }

    private void logErrorContext(HttpServletRequest request, HttpStatus status, Exception e) {
        if (request == null) {
            if (status.is5xxServerError()) {
                log.error("API Error [5xx] - Status: {}, Message: {}", status.value(), e.getMessage(), e);
            } else {
                log.warn("API Error [4xx] - Status: {}, Message: {}", status.value(), e.getMessage());
            }
            return;
        }

        String correlationId = MDC.get("correlationId");
        String uri = request.getRequestURI();
        String method = request.getMethod();
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String user = (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) ? auth.getName() : "anonymous";
        
        if (status.is5xxServerError()) {
            log.error("API Error [5xx] - CorrelationID: {}, Method: {}, URI: {}, User: {}, Status: {}, Message: {}", 
                correlationId, method, uri, user, status.value(), e.getMessage(), e);
        } else {
            log.warn("API Error [4xx] - CorrelationID: {}, Method: {}, URI: {}, User: {}, Status: {}, Message: {}", 
                correlationId, method, uri, user, status.value(), e.getMessage());
        }
    }
}
