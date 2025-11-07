package com.rashid.constants;

// Constants for configuration properties and defaults
public final class ConfigConstants {

    private ConfigConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }

    // Environment variable and property keys
    public static final String ENV_OPENAI_API_KEY = "OPENAI_API_KEY";
    public static final String PROP_OPENAI_API_KEY = "openai.api.key";
    public static final String PROP_OPENAI_MODEL = "openai.model";
    public static final String PROP_OPENAI_MAX_TOKENS = "openai.max.tokens";
    public static final String PROP_OPENAI_TEMPERATURE = "openai.temperature";

    // Default values
    public static final String DEFAULT_MODEL = "gpt-4-turbo-preview";
    public static final int DEFAULT_MAX_TOKENS = 3000;
    public static final double DEFAULT_TEMPERATURE = 0.7;

    // File paths
    public static final String CONFIG_FILE = "application.properties";
}
