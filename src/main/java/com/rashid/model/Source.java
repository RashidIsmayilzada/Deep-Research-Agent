package com.rashid.model;

// Represents a research source with URL and metadata
public class Source {
    private final String title;
    private final String url;

    public Source(String title, String url) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }
}
