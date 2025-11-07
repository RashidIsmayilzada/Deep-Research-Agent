package com.rashid.service.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rashid.constants.PromptConstants;
import com.rashid.exception.AIException;
import com.rashid.service.api.AIClient;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

// Direct HTTP client for OpenAI's search-enabled models using REST API
public class OpenAISearchClient implements AIClient {
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final int CONNECTION_TIMEOUT_SECONDS = 30;
    private static final int REQUEST_TIMEOUT_SECONDS = 120;
    private static final int HTTP_OK = 200;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;
    private final int maxTokens;
    private final double temperature;

    // Constructs OpenAI search client with API key and model settings
    public OpenAISearchClient(String apiKey, String model, int maxTokens, double temperature) {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(CONNECTION_TIMEOUT_SECONDS))
                .build();
        this.objectMapper = new ObjectMapper();
        this.apiKey = apiKey;
        this.model = model;
        this.maxTokens = maxTokens;
        this.temperature = temperature;
    }

    // Sends a chat request with web search capability to OpenAI
    @Override
    public String chatWithSearch(String systemPrompt, String userPrompt) throws AIException {
        try {
            String requestBodyJson = buildRequestBody(systemPrompt, userPrompt);
            HttpRequest request = buildHttpRequest(requestBodyJson);
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            validateResponse(response);
            return parseResponse(response.body());
        } catch (AIException e) {
            throw e;
        } catch (Exception e) {
            throw new AIException("Failed to communicate with AI service", e);
        }
    }

    // Builds the JSON request body for OpenAI API
    private String buildRequestBody(String systemPrompt, String userPrompt) throws AIException {
        try {
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", model);
            requestBody.put("max_tokens", maxTokens);
            requestBody.put("temperature", temperature);

            ArrayNode messages = requestBody.putArray("messages");
            addMessage(messages, PromptConstants.ROLE_SYSTEM, systemPrompt);
            addMessage(messages, PromptConstants.ROLE_USER, userPrompt);

            return objectMapper.writeValueAsString(requestBody);
        } catch (Exception e) {
            throw new AIException("Failed to build request body", e);
        }
    }

    // Adds a message to the messages array
    private void addMessage(ArrayNode messages, String role, String content) {
        ObjectNode message = messages.addObject();
        message.put("role", role);
        message.put("content", content);
    }

    // Builds the HTTP request for OpenAI API
    private HttpRequest buildHttpRequest(String requestBodyJson) {
        return HttpRequest.newBuilder()
                .uri(URI.create(OPENAI_API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .timeout(Duration.ofSeconds(REQUEST_TIMEOUT_SECONDS))
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
                .build();
    }

    // Validates the HTTP response from OpenAI API
    private void validateResponse(HttpResponse<String> response) throws AIException {
        if (response.statusCode() != HTTP_OK) {
            throw new AIException("OpenAI API error: " + response.statusCode() + " - " + response.body(),
                response.statusCode());
        }
    }

    // Parses OpenAI API response and extracts the content
    private String parseResponse(String responseBody) throws AIException {
        try {
            JsonNode root = objectMapper.readTree(responseBody);

            if (root.has("error")) {
                String errorMessage = root.path("error").path("message").asText();
                throw new AIException("OpenAI API error: " + errorMessage);
            }

            return root.path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();
        } catch (Exception e) {
            throw new AIException("Failed to parse AI response", e);
        }
    }

    // Sends a simple chat request with default system prompt
    @Override
    public String chat(String userPrompt) throws AIException {
        return chatWithSearch(
            "You are a professional research assistant with access to current web information. " +
            "Provide comprehensive, well-researched responses with citations when possible.",
            userPrompt
        );
    }
}
