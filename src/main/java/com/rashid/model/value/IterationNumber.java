package com.rashid.model.value;

import com.rashid.exception.ValidationException;

// Value object representing a research iteration number (must be positive)
public class IterationNumber {
    private static final int MIN_VALUE = 1;
    private static final int MAX_VALUE = 100;

    private final int value;

    private IterationNumber(int value) {
        this.value = value;
    }

    // Creates a new IterationNumber after validating the value
    public static IterationNumber create(int value) throws ValidationException {
        if (value < MIN_VALUE) {
            throw new ValidationException(
                String.format("Iteration number must be at least %d, got: %d", MIN_VALUE, value));
        }
        if (value > MAX_VALUE) {
            throw new ValidationException(
                String.format("Iteration number cannot exceed %d, got: %d", MAX_VALUE, value));
        }
        return new IterationNumber(value);
    }

    public int getValue() {
        return value;
    }

    public IterationNumber next() throws ValidationException {
        return create(value + 1);
    }

    @Override
    public String toString() {
        return String.format("Iteration#%d", value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IterationNumber that = (IterationNumber) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }
}
