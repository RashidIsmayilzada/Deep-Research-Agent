package com.rashid.model.value;

import com.rashid.exception.ValidationException;

// Value object representing maximum tokens for AI response
public class MaxTokens {
    private static final int MIN_VALUE = 1;
    private static final int MAX_VALUE = 128000;

    private final int value;

    private MaxTokens(int value) {
        this.value = value;
    }

    // Creates a new MaxTokens after validating the value
    public static MaxTokens create(int value) throws ValidationException {
        if (value < MIN_VALUE || value > MAX_VALUE) {
            throw new ValidationException(
                String.format("Max tokens must be between %d and %d, got: %d",
                    MIN_VALUE, MAX_VALUE, value));
        }
        return new MaxTokens(value);
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("MaxTokens{%d}", value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MaxTokens maxTokens = (MaxTokens) o;
        return value == maxTokens.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }
}
