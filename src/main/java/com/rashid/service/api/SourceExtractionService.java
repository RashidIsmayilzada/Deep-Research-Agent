package com.rashid.service.api;

import com.rashid.model.ResearchIteration;
import com.rashid.model.Source;

import java.util.List;

// Interface for extracting and managing research sources
public interface SourceExtractionService {

    // Extracts source URLs and titles from AI response
    List<Source> extractSourcesFromResponse(String response);

    // Collects all validated sources from all iterations and deduplicates by URL
    List<Source> collectAllSources(List<ResearchIteration> iterations);
}
