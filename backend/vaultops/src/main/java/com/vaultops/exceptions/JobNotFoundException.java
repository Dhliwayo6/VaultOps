package com.vaultops.exceptions;

/**
 * JobNotFoundException will be used for tracking asynchronous job status in Task 05.
 */
public class JobNotFoundException extends RuntimeException {
    public JobNotFoundException(String message) {
        super(message);
    }
}
