package com.rashid.model.value;

import com.rashid.exception.ValidationException;

public class ApiKey {
    private final String value;

    private ApiKey(String value) {
        this.value = value;
    }

    // Creates a new ApiKey after validating the input
    public static ApiKey create(String value) throws ValidationException {
        if (value == null || value.isBlank()) {
            throw new ValidationException("API key cannot be null or empty");
        }
        if (value.length() < 20) {
            throw new ValidationException("API key appears to be invalid (too short)");
        }
        return new ApiKey(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "ApiKey{***" + value.substring(Math.max(0, value.length() - 4)) + "}";
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ApiKey apiKey = (ApiKey) object;
        return value.equals(apiKey.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
