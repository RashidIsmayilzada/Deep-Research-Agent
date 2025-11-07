package com.rashid.service.conversation;

import com.rashid.constants.PromptConstants;
import com.rashid.exception.ResearchException;
import com.rashid.service.api.AIClient;
import com.rashid.service.api.UserClarificationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Service for handling clarification conversation with users
public class ClarificationService implements UserClarificationService {
    private static final int MAX_QUESTIONS = 3;

    private final AIClient aiClient;
    private final Scanner scanner;

    // Constructs clarification service with AI client and scanner
    public ClarificationService(AIClient aiClient, Scanner scanner) {
        this.aiClient = aiClient;
        this.scanner = scanner;
    }

    // AI-driven conversation to clarify what user wants
    @Override
    public String clarifyThroughConversation(String topic) throws ResearchException {
        List<String> conversationHistory = new ArrayList<>();
        conversationHistory.add("User wants to research: " + topic);

        System.out.println("Let me ask you a few questions to understand what you're looking for...\n");

        // Interactive clarification loop - AI asks questions until ready
        for (int questionCount = 0; questionCount < MAX_QUESTIONS; questionCount++) {
            String aiQuestion = generateNextQuestion(topic, conversationHistory, questionCount);

            // Check if AI has gathered sufficient information
            if (isReadyToResearch(aiQuestion)) {
                String instructions = extractInstructions(aiQuestion);
                System.out.println("Got it! I have enough information to start researching.\n");
                return instructions;
            }

            String userResponse = askQuestionAndGetResponse(aiQuestion);
            if (userResponse != null) {
                recordConversationExchange(conversationHistory, aiQuestion, userResponse);
            }
        }

        // Fallback if max questions reached without AI signaling readiness
        return generateInstructionsFromConversation(conversationHistory);
    }

    // AI generates the next clarifying question or signals readiness to research
    private String generateNextQuestion(String topic, List<String> conversation, int questionNum) throws ResearchException {
        StringBuilder conversationContext = new StringBuilder();
        for (String line : conversation) {
            conversationContext.append(line).append("\n");
        }

        String systemPrompt = "You are a research assistant helping to clarify what a user wants to research. " +
            "Based on the conversation so far, decide if you need to ask another clarifying question, " +
            "or if you have enough information to start research.\n\n" +
            "If you need more information, generate ONE specific, natural question to ask the user. " +
            "Make it conversational, not a multiple choice.\n\n" +
            "If you have enough information (usually after 2-3 questions), respond with:\n" +
            "READY: [brief summary of what to research and how, including their level, focus areas, etc.]\n\n" +
            "Example questions:\n" +
            "- 'What specifically interests you about [topic]?'\n" +
            "- 'Are you looking for practical applications or theoretical understanding?'\n" +
            "- 'What's your familiarity with [topic] - should I explain the basics?'\n\n" +
            "Keep questions natural and conversational.";

        String userPrompt = String.format(
            "Topic: %s\n\n" +
            "Conversation so far:\n%s\n\n" +
            "Question #%d: What should I ask next, or am I ready to research?",
            topic,
            conversationContext.toString(),
            questionNum + 1
        );

        return aiClient.chatWithSearch(systemPrompt, userPrompt);
    }

    // Generates research instructions from conversation history
    private String generateInstructionsFromConversation(List<String> conversation) throws ResearchException {
        StringBuilder conversationContext = new StringBuilder();
        for (String line : conversation) {
            conversationContext.append(line).append("\n");
        }

        String systemPrompt = "Based on the conversation with the user, " +
            "summarize what they want researched and how. Include their level, interests, and focus areas.";

        String userPrompt = "Conversation:\n" + conversationContext.toString() + "\n\n" +
            "Summarize the research requirements briefly:";

        return aiClient.chatWithSearch(systemPrompt, userPrompt);
    }

    // Checks if AI is ready to start research
    private boolean isReadyToResearch(String aiQuestion) {
        return aiQuestion.startsWith(PromptConstants.READY_PREFIX);
    }

    // Extracts instructions from AI response
    private String extractInstructions(String aiQuestion) {
        return aiQuestion.substring(PromptConstants.READY_PREFIX.length()).trim();
    }

    // Asks question and gets user response, returns null if empty
    private String askQuestionAndGetResponse(String question) {
        System.out.println("Agent: " + question);
        System.out.print("You: ");
        String response = scanner.nextLine().trim();

        if (response.isEmpty()) {
            System.out.println("(Please provide an answer)");
            return null;
        }
        return response;
    }

    // Records conversation exchange in history
    private void recordConversationExchange(List<String> history, String question, String answer) {
        history.add("Agent asked: " + question);
        history.add("User answered: " + answer);
    }
}
