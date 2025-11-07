package com.rashid.config;

import com.rashid.constants.ConfigConstants;
import com.rashid.exception.ConfigException;
import com.rashid.service.api.ConfigurationService;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.InputStream;
import java.util.Properties;

// Loads application configuration from properties file and .env file
public class ConfigLoader implements ConfigurationService {

    private final Dotenv dotenv;

    // Constructs config loader with dotenv
    public ConfigLoader() {
        this.dotenv = Dotenv.configure().ignoreIfMissing().load();
    }

    // Loads configuration from application.properties file and .env file
    @Override
    public AppConfig load() throws ConfigException {
        Properties props = loadPropertiesFile();
        String apiKey = loadApiKey(props);

        return new AppConfig(
            apiKey,
            props.getProperty(ConfigConstants.PROP_OPENAI_MODEL, ConfigConstants.DEFAULT_MODEL),
            parseMaxTokens(props),
            parseTemperature(props)
        );
    }

    // Loads properties file from classpath
    private Properties loadPropertiesFile() throws ConfigException {
        Properties props = new Properties();

        try (InputStream input = ConfigLoader.class
                .getClassLoader()
                .getResourceAsStream(ConfigConstants.CONFIG_FILE)) {

            if (input == null) {
                throw new ConfigException("Unable to find " + ConfigConstants.CONFIG_FILE);
            }

            props.load(input);
            return props;
        } catch (Exception e) {
            throw new ConfigException("Failed to load configuration file", e);
        }
    }

    // Loads API key from multiple sources (dotenv, system env, properties)
    private String loadApiKey(Properties props) throws ConfigException {
        String apiKey = dotenv.get(ConfigConstants.ENV_OPENAI_API_KEY);

        if (apiKey == null || apiKey.isBlank()) {
            apiKey = System.getenv(ConfigConstants.ENV_OPENAI_API_KEY);
        }

        if (apiKey == null || apiKey.isBlank()) {
            apiKey = props.getProperty(ConfigConstants.PROP_OPENAI_API_KEY);
        }

        if (apiKey == null || apiKey.isBlank()) {
            throw new ConfigException(
                "OpenAI API key not found. Create a .env file with " +
                ConfigConstants.ENV_OPENAI_API_KEY +
                " or set it as an environment variable."
            );
        }

        return apiKey;
    }

    // Parses max tokens from properties
    private int parseMaxTokens(Properties props) throws ConfigException {
        try {
            String value = props.getProperty(
                ConfigConstants.PROP_OPENAI_MAX_TOKENS,
                String.valueOf(ConfigConstants.DEFAULT_MAX_TOKENS)
            );
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ConfigException("Invalid max tokens configuration", e);
        }
    }

    // Parses temperature from properties
    private double parseTemperature(Properties props) throws ConfigException {
        try {
            String value = props.getProperty(
                ConfigConstants.PROP_OPENAI_TEMPERATURE,
                String.valueOf(ConfigConstants.DEFAULT_TEMPERATURE)
            );
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new ConfigException("Invalid temperature configuration", e);
        }
    }
}
