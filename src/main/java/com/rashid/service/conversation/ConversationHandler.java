package com.rashid.service.conversation;

import com.rashid.constants.PromptConstants;
import com.rashid.exception.ResearchException;
import com.rashid.model.ResearchIteration;
import com.rashid.service.api.AIClient;
import com.rashid.service.api.ConversationService;

import java.util.List;
import java.util.Scanner;

// Service for handling follow-up conversations after research
public class ConversationHandler implements ConversationService {
    private static final int CONTEXT_TRUNCATION_LENGTH = 1500;

    private final AIClient aiClient;
    private final Scanner scanner;

    // Constructs conversation handler with AI client and scanner
    public ConversationHandler(AIClient aiClient, Scanner scanner) {
        this.aiClient = aiClient;
        this.scanner = scanner;
    }

    // Handles unlimited follow-up conversation after initial research
    @Override
    public void continuousConversation(String topic, String previousFindings, List<ResearchIteration> iterations) throws ResearchException {
        StringBuilder fullContext = new StringBuilder(previousFindings);

        while (true) {
            displayFollowUpPrompt();
            String userInput = scanner.nextLine().trim();

            if (userInput.isEmpty()) {
                continue;
            }

            if (isExitCommand(userInput)) {
                System.out.println("Research session complete!");
                break;
            }

            String aiResponse = answerFollowUp(topic, userInput, fullContext.toString());
            displayFollowUpResponse(aiResponse);
            updateConversationContext(fullContext, userInput, aiResponse);
        }
    }

    // Generates AI response to a follow-up question with fresh web research
    private String answerFollowUp(String topic, String question, String context) throws ResearchException {
        String systemPrompt = "You are an expert research assistant with access to real-time web search. " +
            "The user has been researching " + topic + ". " +
            "IMPORTANT: You MUST search the web for current information to answer their question. " +
            "Do NOT just use the previous research context - always search for NEW, updated information. " +
            "Provide comprehensive answers with citations and URLs.";

        String userPrompt = String.format(
            "Previous research context:\n%s\n\n" +
            "User's NEW question: %s\n\n" +
            "TASK: Search the web RIGHT NOW for current information to answer this question. " +
            "Provide a comprehensive answer with:\n" +
            "- Latest data and findings from web search\n" +
            "- Specific sources with URLs\n" +
            "- How this relates to the previous research if relevant\n\n" +
            "Do NOT just summarize the previous research - SEARCH for new information!",
            truncate(context, CONTEXT_TRUNCATION_LENGTH),
            question
        );

        return aiClient.chatWithSearch(systemPrompt, userPrompt);
    }

    // Displays follow-up question prompt
    private void displayFollowUpPrompt() {
        System.out.println("\n" + "─".repeat(80));
        System.out.println("Ask me anything else, or type 'done' when finished");
        System.out.println("─".repeat(80));
        System.out.print("\nYou: ");
    }

    // Checks if user wants to exit conversation
    private boolean isExitCommand(String input) {
        return input.equalsIgnoreCase(PromptConstants.EXIT_DONE) ||
               input.equalsIgnoreCase(PromptConstants.EXIT_EXIT) ||
               input.equalsIgnoreCase(PromptConstants.EXIT_QUIT);
    }

    // Displays follow-up response
    private void displayFollowUpResponse(String response) {
        System.out.println("\nAgent: " + response);
        System.out.println("─".repeat(80));
    }

    // Updates conversation context with new exchange
    private void updateConversationContext(StringBuilder context, String question, String answer) {
        context.append("\n\nUser asked: ").append(question);
        context.append("\nAgent answered: ").append(answer);
    }

    // Truncates text to avoid exceeding token limits
    private String truncate(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(text.length() - maxLength) + "\n[Earlier context truncated]";
    }
}
