package com.rashid.model.value;

import com.rashid.exception.ValidationException;

import java.net.URI;
import java.net.URISyntaxException;

// Value object representing a validated URL for research sources
public class ResearchUrl {
    private final String value;

    private ResearchUrl(String value) {
        this.value = value;
    }

    // Creates a new ResearchUrl after validating the URL format
    public static ResearchUrl create(String url) throws ValidationException {
        if (url == null || url.isBlank()) {
            throw new ValidationException("URL cannot be null or empty");
        }

        try {
            URI uri = new URI(url);
            if (uri.getScheme() == null) {
                throw new ValidationException("URL must have a scheme (http or https)");
            }
            if (!uri.getScheme().equals("http") && !uri.getScheme().equals("https")) {
                throw new ValidationException("URL scheme must be http or https, got: " + uri.getScheme());
            }
            if (uri.getHost() == null) {
                throw new ValidationException("URL must have a host");
            }
            return new ResearchUrl(url);
        } catch (URISyntaxException e) {
            throw new ValidationException("Invalid URL format: " + url, e);
        }
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResearchUrl that = (ResearchUrl) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
