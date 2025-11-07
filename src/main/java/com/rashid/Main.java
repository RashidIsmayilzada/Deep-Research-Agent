package com.rashid;

import com.rashid.config.AppConfig;
import com.rashid.config.ConfigLoader;
import com.rashid.exception.ConfigException;
import com.rashid.service.agent.ResearchAgent;
import com.rashid.service.ai.OpenAISearchClient;
import com.rashid.service.api.AIClient;
import com.rashid.service.api.ConversationService;
import com.rashid.service.api.ResearchService;
import com.rashid.service.api.SourceExtractionService;
import com.rashid.service.api.UserClarificationService;
import com.rashid.service.conversation.ClarificationService;
import com.rashid.service.conversation.ConversationHandler;
import com.rashid.service.research.*;
import com.rashid.service.validation.SourceValidator;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("Deep Research Agent - AI-Driven Conversational Research");
        System.out.println("=".repeat(80));
        System.out.println();

        try (Scanner scanner = new Scanner(System.in)) {
            // Load configuration
            ConfigLoader configLoader = new ConfigLoader();
            AppConfig config = configLoader.load();

            // Create AI client
            AIClient aiClient = new OpenAISearchClient(
                config.getOpenAiApiKey(),
                config.getOpenAiModel(),
                config.getOpenAiMaxTokens(),
                config.getOpenAiTemperature()
            );

            // Create research services
            GapAnalyzer gapAnalyzer = new GapAnalyzer(aiClient);
            QueryGenerator queryGenerator = new QueryGenerator(aiClient);
            SynthesisService synthesisService = new SynthesisService(aiClient);
            SourceValidator sourceValidator = new SourceValidator();
            SourceExtractionService sourceExtractor = new SourceExtractor(sourceValidator);

            // Create main research orchestrator
            ResearchService researchService = new ResearchOrchestrator(
                aiClient,
                gapAnalyzer,
                queryGenerator,
                synthesisService,
                sourceExtractor
            );

            // Create conversation services
            UserClarificationService clarificationService = new ClarificationService(aiClient, scanner);
            ConversationService conversationService = new ConversationHandler(aiClient, scanner);

            // Create research agent with all dependencies injected
            ResearchAgent agent = new ResearchAgent(
                clarificationService,
                researchService,
                conversationService,
                sourceExtractor
            );

            System.out.println("I'll ask clarifying questions, conduct deep iterative research,");
            System.out.println("and answer any follow-up questions you have.\n");

            runResearchLoop(agent, scanner);

        } catch (ConfigException e) {
            System.err.println("Configuration error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Main research loop that processes user topics until quit
    private static void runResearchLoop(ResearchAgent agent, Scanner scanner) {
        while (true) {
            System.out.println();
            System.out.print("Enter research topic (or 'quit' to exit): ");
            String topic = scanner.nextLine().trim();

            if (topic.equalsIgnoreCase("quit") || topic.equalsIgnoreCase("exit")) {
                System.out.println("\nThank you for using Deep Research Agent!");
                break;
            }

            if (topic.isEmpty()) {
                System.out.println("Please enter a valid topic.");
                continue;
            }

            agent.startConversation(topic);
        }
    }
}
