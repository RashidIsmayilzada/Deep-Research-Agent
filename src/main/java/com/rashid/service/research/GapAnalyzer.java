package com.rashid.service.research;

import com.rashid.constants.PromptConstants;
import com.rashid.exception.ResearchException;
import com.rashid.service.api.AIClient;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Service for analyzing research findings to identify knowledge gaps
public class GapAnalyzer {
    private static final int MAX_GAPS = 5;
    private static final int FINDINGS_TRUNCATION_LENGTH = 2000;

    private final AIClient aiClient;

    public GapAnalyzer(AIClient aiClient) {
        this.aiClient = aiClient;
    }

    // Analyzes gaps while considering user's specific requirements
    public List<String> analyzeGapsWithInstructions(String topic, String userInstructions,
            String findings, int iteration) throws ResearchException {
        String systemPrompt = "You are a critical research analyst. Identify gaps based on what the user wants to know.";

        String userPrompt = String.format(
            "Topic: %s\n\n" +
            "What the user specifically wants to know:\n%s\n\n" +
            "Current findings (Iteration %d):\n%s\n\n" +
            "Identify 3-5 gaps considering the user's specific requirements above:\n\n" +
            "- Are all aspects the user cares about covered?\n" +
            "- Missing data, statistics, or evidence?\n" +
            "- Unexplored perspectives or contradictions?\n" +
            "- Technical details needed?\n" +
            "- Real-world examples or applications?\n\n" +
            "Format each: 'GAP: [specific description]'",
            topic,
            userInstructions,
            iteration,
            truncate(findings, FINDINGS_TRUNCATION_LENGTH)
        );

        String response = aiClient.chat(userPrompt);
        return extractGapsFromResponse(response);
    }

    // Forces AI to generate deep-dive topics when no gaps are found
    public List<String> forceDeepDiveTopics(String topic, String userInstructions,
            String findings, int iteration) throws ResearchException {
        String systemPrompt = "You are a research strategist. Even when research seems complete, there are ALWAYS " +
            "deeper aspects to explore. Generate topics for deeper investigation.";

        String userPrompt = String.format(
            "Topic: %s\n\n" +
            "User wants: %s\n\n" +
            "Current findings (Iteration %d):\n%s\n\n" +
            "The AI thinks the research is complete, but we need to go DEEPER for iteration %d.\n" +
            "Generate 2-3 topics that would benefit from deeper investigation:\n\n" +
            "Consider exploring:\n" +
            "- Alternative perspectives not yet covered\n" +
            "- Specific technical details or mechanisms\n" +
            "- Recent developments or future trends\n" +
            "- Comparative analysis with similar topics\n" +
            "- Practical applications or case studies\n" +
            "- Expert debates or controversies\n\n" +
            "Format each: 'GAP: [specific topic to explore deeper]'\n" +
            "Generate at least 2 topics even if you think research is comprehensive.",
            topic,
            userInstructions,
            iteration,
            truncate(findings, 1500),
            iteration + 1
        );

        String response = aiClient.chat(userPrompt);
        List<String> gaps = extractGapsFromResponse(response);

        // Absolute fallback: if AI still doesn't generate gaps, create generic ones
        if (gaps.isEmpty()) {
            gaps.add("Latest developments and recent research on " + topic);
            gaps.add("Expert opinions and different perspectives on " + topic);
        }

        return gaps;
    }

    // Extracts gaps from AI response
    public List<String> extractGapsFromResponse(String response) {
        List<String> gaps = new ArrayList<>();
        Pattern pattern = Pattern.compile("(?i)" + PromptConstants.GAP_PREFIX + "\\s*(.+?)(?=\\n|$)", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(response);

        while (matcher.find() && gaps.size() < MAX_GAPS) {
            String gap = matcher.group(1).trim();
            if (!gap.isEmpty()) {
                gaps.add(gap);
            }
        }
        return gaps;
    }

    // Truncates text to avoid exceeding token limits
    private String truncate(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(text.length() - maxLength) + "\n[Earlier context truncated]";
    }
}
