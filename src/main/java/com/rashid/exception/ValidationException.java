package com.rashid.exception;

// Exception for validation errors (invalid URLs, parameters, etc.)
public class ValidationException extends ResearchException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
