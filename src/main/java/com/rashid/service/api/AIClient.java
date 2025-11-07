package com.rashid.service.api;

import com.rashid.exception.AIException;

// Interface for AI chat clients (OpenAI, Claude, Gemini, etc.)
public interface AIClient {

    // Sends a chat request with web search capability
    String chatWithSearch(String systemPrompt, String userPrompt) throws AIException;

    // Sends a simple chat request with default system prompt
    String chat(String userPrompt) throws AIException;
}
