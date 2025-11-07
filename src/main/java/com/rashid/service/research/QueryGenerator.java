package com.rashid.service.research;

import com.rashid.constants.PromptConstants;
import com.rashid.exception.ResearchException;
import com.rashid.service.api.AIClient;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Service for generating follow-up research queries based on identified gaps
public class QueryGenerator {
    private static final int MAX_QUERIES = 3;

    private final AIClient aiClient;

    public QueryGenerator(AIClient aiClient) {
        this.aiClient = aiClient;
    }

    // Generates queries considering user's specific interests
    public List<String> generateQueriesWithInstructions(String topic, String userInstructions,
            List<String> gaps, String findings) throws ResearchException {
        String systemPrompt = "You are a research query expert. Generate targeted follow-up queries.";

        String userPrompt = String.format(
            "Topic: %s\n\n" +
            "User's specific interests:\n%s\n\n" +
            "Identified gaps:\n%s\n\n" +
            "Generate 2-3 specific follow-up search queries that:\n" +
            "1. Address the gaps\n" +
            "2. Align with user's interests ('%s')\n" +
            "3. Will find NEW information not already covered\n\n" +
            "Format each: 'QUERY: [search query]'",
            topic,
            userInstructions,
            String.join("\n", gaps),
            userInstructions
        );

        String response = aiClient.chat(userPrompt);
        return extractQueriesFromResponse(response);
    }

    // Extracts queries from AI response
    public List<String> extractQueriesFromResponse(String response) {
        List<String> queries = new ArrayList<>();
        Pattern pattern = Pattern.compile("(?i)" + PromptConstants.QUERY_PREFIX + "\\s*(.+?)(?=\\n|$)", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(response);

        while (matcher.find() && queries.size() < MAX_QUERIES) {
            String query = matcher.group(1).trim();
            if (!query.isEmpty()) {
                queries.add(query);
            }
        }
        return queries;
    }
}
