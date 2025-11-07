package com.rashid.service.api;

import com.rashid.exception.ResearchException;
import com.rashid.model.ResearchIteration;
import com.rashid.model.Source;

import java.util.List;

// Interface for conducting iterative research
public interface ResearchService {

    // Conducts deep iterative research with adaptive iterations
    List<ResearchIteration> conductDeepIterativeResearch(String topic, String instructions) throws ResearchException;

    // Synthesizes findings from all iterations into a comprehensive report
    String synthesizeAllIterations(String topic, List<ResearchIteration> iterations) throws ResearchException;

    // Extracts key findings from the final synthesis
    String extractKeyFindings(String topic, String synthesis) throws ResearchException;
}
