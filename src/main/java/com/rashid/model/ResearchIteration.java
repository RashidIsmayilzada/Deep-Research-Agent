package com.rashid.model;

import java.util.List;

// Represents a single iteration in the deep research process
public class ResearchIteration {
    private final int iterationNumber;
    private final String findings;
    private final List<String> identifiedGaps;
    private final List<String> followUpQueries;
    private final List<Source> sources;

    public ResearchIteration(int iterationNumber, String findings,
                             List<String> identifiedGaps, List<String> followUpQueries,
                             List<Source> sources) {
        this.iterationNumber = iterationNumber;
        this.findings = findings;
        this.identifiedGaps = identifiedGaps;
        this.followUpQueries = followUpQueries;
        this.sources = sources;
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
