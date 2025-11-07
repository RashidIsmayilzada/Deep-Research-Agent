package com.rashid.exception;

// Base exception for all research-related errors
public class ResearchException extends Exception {

    public ResearchException(String message) {
        super(message);
    }

    public ResearchException(String message, Throwable cause) {
        super(message, cause);
    }
}
