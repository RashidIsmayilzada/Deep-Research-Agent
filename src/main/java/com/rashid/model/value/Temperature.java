package com.rashid.model.value;

import com.rashid.exception.ValidationException;

// Value object representing AI temperature (0.0 - 2.0)
public class Temperature {
    private static final double MIN_VALUE = 0.0;
    private static final double MAX_VALUE = 2.0;

    private final double value;

    private Temperature(double value) {
        this.value = value;
    }

    // Creates a new Temperature after validating the value
    public static Temperature create(double value) throws ValidationException {
        if (value < MIN_VALUE || value > MAX_VALUE) {
            throw new ValidationException(
                String.format("Temperature must be between %.1f and %.1f, got: %.2f",
                    MIN_VALUE, MAX_VALUE, value));
        }
        return new Temperature(value);
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("Temperature{%.2f}", value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Temperature that = (Temperature) o;
        return Double.compare(that.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(value);
    }
}
