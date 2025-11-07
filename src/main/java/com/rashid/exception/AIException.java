package com.rashid.exception;

// Exception for AI service errors (API calls, timeouts, rate limits)
public class AIException extends ResearchException {

    private final Integer statusCode;

    public AIException(String message) {
        super(message);
        this.statusCode = null;
    }

    public AIException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = null;
    }

    public AIException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}
