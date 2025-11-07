package com.rashid.config;

// Application configuration holder containing OpenAI settings
public class AppConfig {
    private final String openAiApiKey;
    private final String openAiModel;
    private final int openAiMaxTokens;
    private final double openAiTemperature;

    // Constructs a new application configuration with OpenAI settings
    public AppConfig(
            String openAiApiKey,
            String openAiModel,
            int openAiMaxTokens,
            double openAiTemperature
    ) {
        this.openAiApiKey = openAiApiKey;
        this.openAiModel = openAiModel;
        this.openAiMaxTokens = openAiMaxTokens;
        this.openAiTemperature = openAiTemperature;
    }

    public String getOpenAiApiKey() {
        return openAiApiKey;
    }

    public String getOpenAiModel() {
        return openAiModel;
    }

    public int getOpenAiMaxTokens() {
        return openAiMaxTokens;
    }

    public double getOpenAiTemperature() {
        return openAiTemperature;
    }
}
