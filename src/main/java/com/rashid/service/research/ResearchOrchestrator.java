package com.rashid.service.research;

import com.rashid.constants.PromptConstants;
import com.rashid.exception.ResearchException;
import com.rashid.model.ResearchIteration;
import com.rashid.model.Source;
import com.rashid.service.api.AIClient;
import com.rashid.service.api.ResearchService;
import com.rashid.service.api.SourceExtractionService;

import java.util.ArrayList;
import java.util.List;

// Orchestrates the deep iterative research process
public class ResearchOrchestrator implements ResearchService {
    private static final int MIN_GUARANTEED_ITERATIONS = 3;
    private static final int MAX_SAFETY_ITERATIONS = 10;
    private static final int FINDINGS_TRUNCATION_LENGTH = 2000;

    private final AIClient aiClient;
    private final GapAnalyzer gapAnalyzer;
    private final QueryGenerator queryGenerator;
    private final SynthesisService synthesisService;
    private final SourceExtractionService sourceExtractor;

    public ResearchOrchestrator(AIClient aiClient, GapAnalyzer gapAnalyzer,
                               QueryGenerator queryGenerator, SynthesisService synthesisService,
                               SourceExtractionService sourceExtractor) {
        this.aiClient = aiClient;
        this.gapAnalyzer = gapAnalyzer;
        this.queryGenerator = queryGenerator;
        this.synthesisService = synthesisService;
        this.sourceExtractor = sourceExtractor;
    }

    // Conducts deep iterative research with ADAPTIVE iterations (AI decides when to stop)
    @Override
    public List<ResearchIteration> conductDeepIterativeResearch(String topic, String instructions) throws ResearchException {
        List<ResearchIteration> iterations = new ArrayList<>();
        String accumulatedFindings = "";
        String currentQuery = topic;

        // Main research loop - continues until AI decides research is complete or limit reached
        for (int iterationNum = 1; iterationNum <= MAX_SAFETY_ITERATIONS; iterationNum++) {
            ResearchIteration iteration = executeResearchIteration(
                topic, instructions, currentQuery, accumulatedFindings, iterationNum);
            iterations.add(iteration);

            accumulatedFindings = updateAccumulatedFindings(accumulatedFindings, iteration.getFindings(), iterationNum);

            if (shouldStopResearch(iterationNum, iteration, topic, instructions, accumulatedFindings)) {
                break;
            }

            currentQuery = determineNextQuery(topic, iteration.getFollowUpQueries());
        }

        return iterations;
    }

    // Executes a single research iteration
    private ResearchIteration executeResearchIteration(String topic, String instructions,
            String currentQuery, String accumulatedFindings, int iterationNum) throws ResearchException {
        String findings = conductResearchIteration(currentQuery, instructions, iterationNum, accumulatedFindings);
        List<Source> sources = sourceExtractor.extractSourcesFromResponse(findings);
        List<String> gaps = identifyResearchGaps(topic, instructions, accumulatedFindings, iterationNum);
        List<String> followUpQueries = generateFollowUpQueries(topic, instructions, gaps, accumulatedFindings, iterationNum);

        return new ResearchIteration(iterationNum, findings, gaps, followUpQueries, sources);
    }

    // Updates accumulated findings with new iteration results
    private String updateAccumulatedFindings(String accumulated, String newFindings, int iterationNum) {
        if (accumulated.isEmpty()) {
            return newFindings;
        }
        return accumulated + "\n\n--- Iteration " + iterationNum + " Findings ---\n\n" + newFindings;
    }

    // Identifies research gaps for current iteration
    private List<String> identifyResearchGaps(String topic, String instructions,
            String accumulatedFindings, int iterationNum) throws ResearchException {
        List<String> gaps = gapAnalyzer.analyzeGapsWithInstructions(topic, instructions, accumulatedFindings, iterationNum);

        // Force deep dive topics for early iterations to ensure comprehensive research
        if (iterationNum < MIN_GUARANTEED_ITERATIONS && gaps.isEmpty()) {
            gaps = gapAnalyzer.forceDeepDiveTopics(topic, instructions, accumulatedFindings, iterationNum);
        }
        return gaps;
    }

    // Generates follow-up queries based on gaps
    private List<String> generateFollowUpQueries(String topic, String instructions,
            List<String> gaps, String accumulatedFindings, int iterationNum) throws ResearchException {
        if (iterationNum < MIN_GUARANTEED_ITERATIONS || !gaps.isEmpty()) {
            return queryGenerator.generateQueriesWithInstructions(topic, instructions, gaps, accumulatedFindings);
        }
        return new ArrayList<>();
    }

    // AI decides whether to continue researching or if it's comprehensive enough
    private boolean aiDecideToContinue(String topic, String userInstructions, String findings,
            List<String> gaps, int iterationNum) throws ResearchException {
        String systemPrompt = "You are a research quality evaluator. Decide if the research is comprehensive enough " +
            "or if more iteration is needed.";

        String userPrompt = String.format(
            "Topic: %s\n\n" +
            "User wants to know about:\n%s\n\n" +
            "Current findings after %d iteration(s):\n%s\n\n" +
            "Identified gaps: %s\n\n" +
            "Should we continue researching? Consider:\n" +
            "1. Are user's requirements ('%s') fully addressed?\n" +
            "2. Are there significant knowledge gaps remaining?\n" +
            "3. Is the research comprehensive and authoritative?\n" +
            "4. Have we covered multiple perspectives and sources?\n" +
            "5. Are contradictions explored and explained?\n\n" +
            "Respond with EXACTLY one word:\n" +
            "- 'CONTINUE' if more research is needed\n" +
            "- 'COMPLETE' if research is comprehensive enough",
            topic,
            userInstructions,
            iterationNum,
            truncate(findings, FINDINGS_TRUNCATION_LENGTH),
            gaps.isEmpty() ? "None" : String.join(", ", gaps),
            userInstructions
        );

        String decision = aiClient.chat(userPrompt).trim().toUpperCase();
        return decision.contains(PromptConstants.DECISION_CONTINUE);
    }

    // Determines the next query to research
    private String determineNextQuery(String topic, List<String> followUpQueries) {
        if (followUpQueries.isEmpty()) {
            return topic + " - deeper analysis and additional perspectives";
        }
        return followUpQueries.get(0);
    }

    // Determines if research should stop
    private boolean shouldStopResearch(int iterationNum, ResearchIteration iteration,
            String topic, String instructions, String accumulatedFindings) throws ResearchException {
        // Guarantee minimum iterations
        if (iterationNum < MIN_GUARANTEED_ITERATIONS) {
            return false;
        }

        // Stop if no follow-up queries
        if (iteration.getFollowUpQueries().isEmpty()) {
            return true;
        }

        // Let AI decide if research is comprehensive enough
        return !aiDecideToContinue(topic, instructions, accumulatedFindings,
            iteration.getIdentifiedGaps(), iterationNum);
    }

    // Conducts research for a single iteration
    private String conductResearchIteration(String query, String instructions, int iterationNum,
            String previousFindings) throws ResearchException {
        String systemPrompt = "You are an expert research analyst with access to current web information. " +
            "Search the web for the most current and authoritative information. " +
            "User research requirements:\n" + instructions + "\n\n" +
            "Provide comprehensive, well-structured findings with proper citations. " +
            "Include specific sources, URLs, and publication dates where relevant.";

        String userPrompt;
        if (iterationNum == 1) {
            userPrompt = buildInitialResearchPrompt(query);
        } else {
            userPrompt = buildDeepDivePrompt(query, previousFindings);
        }

        return aiClient.chatWithSearch(systemPrompt, userPrompt);
    }

    // Builds comprehensive initial research prompt
    private String buildInitialResearchPrompt(String topic) {
        return String.format(
            "Conduct comprehensive web research on: %s\n\n" +
            "Search the web and provide:\n" +
            "1. **Overview**: Current understanding and definition\n" +
            "2. **Historical Context**: Key developments and milestones\n" +
            "3. **Current State**: Latest developments, statistics, and trends (2024-2025)\n" +
            "4. **Key Concepts**: Main principles and important details\n" +
            "5. **Applications & Impact**: Real-world uses and implications\n" +
            "6. **Expert Opinions**: What leading authorities say\n" +
            "7. **Debates & Contradictions**: Different viewpoints if they exist\n\n" +
            "Include specific citations with URLs and publication dates. " +
            "Focus on authoritative, recent sources.\n" +
            "Format professionally with clear sections.",
            topic
        );
    }

    // Builds focused deep dive prompt for follow-up research
    private String buildDeepDivePrompt(String focusQuery, String previousFindings) {
        return String.format(
            "Conduct focused deep research on: %s\n\n" +
            "Previous research context (avoid repeating):\n%s\n\n" +
            "This is a follow-up investigation to address specific knowledge gaps.\n" +
            "Search for:\n" +
            "- Latest data and statistics not yet covered\n" +
            "- Expert analyses and opinions\n" +
            "- Contradicting viewpoints or debates\n" +
            "- Technical details and mechanisms\n" +
            "- Real-world examples and case studies\n" +
            "- Recent developments (2024-2025)\n\n" +
            "Provide detailed findings with specific citations, URLs, and dates.\n" +
            "Be thorough and evidence-based. Focus on NEW information not in previous research.",
            focusQuery,
            truncate(previousFindings, 1500)
        );
    }

    // Synthesizes findings from all iterations into a comprehensive report
    @Override
    public String synthesizeAllIterations(String topic, List<ResearchIteration> iterations) throws ResearchException {
        return synthesisService.synthesizeAllIterations(topic, iterations);
    }

    // Extracts key findings from the final synthesis
    @Override
    public String extractKeyFindings(String topic, String synthesis) throws ResearchException {
        return synthesisService.extractKeyFindings(topic, synthesis);
    }

    // Truncates text to avoid exceeding token limits
    private String truncate(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(text.length() - maxLength) + "\n[Earlier context truncated]";
    }
}
