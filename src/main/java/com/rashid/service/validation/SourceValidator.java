package com.rashid.service.validation;

import java.net.URI;

// Validates URLs for well-formedness
public class SourceValidator {

    // Checks if a URL appears to be valid and well-formed
    public boolean isWellFormedUrl(String url) {
        try {
            URI uri = URI.create(url);
            return uri.getScheme() != null &&
                   (uri.getScheme().equals("http") || uri.getScheme().equals("https")) &&
                   uri.getHost() != null;
        } catch (Exception e) {
            return false;
        }
    }
}
