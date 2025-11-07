package com.rashid.service.research;

import com.rashid.model.ResearchIteration;
import com.rashid.model.Source;
import com.rashid.service.api.SourceExtractionService;
import com.rashid.service.validation.SourceValidator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Service for extracting and managing research sources
public class SourceExtractor implements SourceExtractionService {
    private static final int MAX_SOURCES_PER_ITERATION = 15;
    private static final Pattern MARKDOWN_LINK_PATTERN = Pattern.compile("\\[([^\\]]+)\\]\\((https?://[^)]+)\\)");

    private final SourceValidator sourceValidator;

    // Constructs source extractor with validator
    public SourceExtractor(SourceValidator sourceValidator) {
        this.sourceValidator = sourceValidator;
    }

    // Extracts source URLs and titles from AI response (only markdown format)
    @Override
    public List<Source> extractSourcesFromResponse(String response) {
        List<Source> sources = new ArrayList<>();
        Map<String, String> urlToTitle = new LinkedHashMap<>();

        // Extract markdown-style links [title](url)
        Matcher markdownMatcher = MARKDOWN_LINK_PATTERN.matcher(response);

        while (markdownMatcher.find()) {
            String title = markdownMatcher.group(1).trim();
            String url = cleanUrl(markdownMatcher.group(2));
            if (!title.isEmpty() && !url.isEmpty() && sourceValidator.isWellFormedUrl(url)) {
                urlToTitle.put(url, title);
            }
        }

        // Create Source objects from collected URLs
        for (Map.Entry<String, String> entry : urlToTitle.entrySet()) {
            if (sources.size() >= MAX_SOURCES_PER_ITERATION) break;
            sources.add(new Source(entry.getValue(), entry.getKey()));
        }

        return sources;
    }

    // Collects all validated sources from all iterations and deduplicates by URL
    @Override
    public List<Source> collectAllSources(List<ResearchIteration> iterations) {
        Map<String, Source> uniqueSources = new LinkedHashMap<>();

        for (ResearchIteration iteration : iterations) {
            for (Source source : iteration.getSources()) {
                uniqueSources.putIfAbsent(source.getUrl(), source);
            }
        }

        return new ArrayList<>(uniqueSources.values());
    }

    // Cleans URL by removing trailing markdown artifacts
    private String cleanUrl(String url) {
        return url.replaceAll("[)\\]]+$", "").trim();
    }
}
