package com.rashid.exception;

// Exception for configuration errors (missing API keys, invalid config)
public class ConfigException extends ResearchException {

    public ConfigException(String message) {
        super(message);
    }

    public ConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
