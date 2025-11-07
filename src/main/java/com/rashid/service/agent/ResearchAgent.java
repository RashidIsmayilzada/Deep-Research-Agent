package com.rashid.service.agent;

import com.rashid.exception.ResearchException;
import com.rashid.model.ResearchIteration;
import com.rashid.model.Source;
import com.rashid.service.api.ConversationService;
import com.rashid.service.api.ResearchService;
import com.rashid.service.api.SourceExtractionService;
import com.rashid.service.api.UserClarificationService;

import java.util.List;

// Deep research agent with AI-driven questions and iterative research
public class ResearchAgent {
    private final UserClarificationService clarificationService;
    private final ResearchService researchService;
    private final ConversationService conversationService;
    private final SourceExtractionService sourceExtractor;

    // Constructs research agent with all dependencies injected
    public ResearchAgent(UserClarificationService clarificationService,
                        ResearchService researchService,
                        ConversationService conversationService,
                        SourceExtractionService sourceExtractor) {
        this.clarificationService = clarificationService;
        this.researchService = researchService;
        this.conversationService = conversationService;
        this.sourceExtractor = sourceExtractor;
    }

    // Starts a natural conversation to understand user's research needs
    public void startConversation(String initialTopic) {
        try {
            displayResearchHeader();
            String researchInstructions = clarificationService.clarifyThroughConversation(initialTopic);
            System.out.println("Researching...");

            ResearchResults results = executeResearchWithTimer(initialTopic, researchInstructions);
            displayResearchResults(results);
            conversationService.continuousConversation(initialTopic, results.finalSynthesis(), results.iterations());

        } catch (ResearchException e) {
            System.err.println("Research error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Displays the research mode header
    private void displayResearchHeader() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("CONVERSATIONAL RESEARCH MODE");
        System.out.println("=".repeat(80));
    }

    // Executes research with timer and returns results
    private ResearchResults executeResearchWithTimer(String topic, String instructions) throws ResearchException {
        long researchStartTime = System.currentTimeMillis();
        Thread timerThread = startDynamicTimer(researchStartTime);

        try {
            List<ResearchIteration> iterations = researchService.conductDeepIterativeResearch(topic, instructions);
            String finalSynthesis = researchService.synthesizeAllIterations(topic, iterations);
            String keyFindings = researchService.extractKeyFindings(topic, finalSynthesis);
            List<Source> allSources = sourceExtractor.collectAllSources(iterations);
            long totalTime = System.currentTimeMillis() - researchStartTime;

            return new ResearchResults(iterations, finalSynthesis, keyFindings, allSources, totalTime);
        } finally {
            timerThread.interrupt();
            System.out.print("\r                    \r");
        }
    }

    // Displays research results in formatted output
    private void displayResearchResults(ResearchResults results) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("RESEARCH FINDINGS");
        System.out.printf("Iterations: %d | Time: %.1fs\n",
            results.iterations().size(), results.totalTime() / 1000.0);
        System.out.println("=".repeat(80));

        System.out.println("\n--- FINAL SYNTHESIS ---");
        System.out.println(results.finalSynthesis());

        System.out.println("\n--- KEY FINDINGS ---");
        System.out.println(results.keyFindings());

        displaySources(results.allSources());
        System.out.println("=".repeat(80));
    }

    // Displays the list of sources
    private void displaySources(List<Source> sources) {
        System.out.println("\n--- SOURCES (" + sources.size() + ") ---");
        for (int i = 0; i < sources.size(); i++) {
            Source source = sources.get(i);
            System.out.printf("%d. %s\n   %s\n", i + 1, source.getTitle(), source.getUrl());
        }
    }

    // Starts a dynamic timer thread that updates every second
    private Thread startDynamicTimer(long startTime) {
        Thread timerThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    long elapsed = (System.currentTimeMillis() - startTime) / 1000;
                    System.out.print(String.format("\r%ds", elapsed));
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        timerThread.setDaemon(true);
        timerThread.start();
        return timerThread;
    }

    // Record class to hold research results
    private record ResearchResults(
        List<ResearchIteration> iterations,
        String finalSynthesis,
        String keyFindings,
        List<Source> allSources,
        long totalTime
    ) {}
}
