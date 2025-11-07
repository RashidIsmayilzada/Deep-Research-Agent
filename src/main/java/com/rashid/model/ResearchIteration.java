package com.rashid.model;

import java.util.List;

// Represents a single iteration in the deep research process
public class ResearchIteration {
    private final int iterationNumber;
    private final String query;
    private final String findings;
    private final List<String> identifiedGaps;
    private final List<String> followUpQueries;
    private final List<Source> sources;
    private final long durationMs;

    public ResearchIteration(int iterationNumber, String query, String findings,
                             List<String> identifiedGaps, List<String> followUpQueries,
                             List<Source> sources, long durationMs) {
        this.iterationNumber = iterationNumber;
        this.query = query;
        this.findings = findings;
        this.identifiedGaps = identifiedGaps;
        this.followUpQueries = followUpQueries;
        this.sources = sources;
        this.durationMs = durationMs;
    }

    public int getIterationNumber() {
        return iterationNumber;
    }

    public String getFindings() {
        return findings;
    }

    public List<String> getIdentifiedGaps() {
        return identifiedGaps;
    }

    public List<String> getFollowUpQueries() {
        return followUpQueries;
    }

    public List<Source> getSources() {
        return sources;
    }
}
