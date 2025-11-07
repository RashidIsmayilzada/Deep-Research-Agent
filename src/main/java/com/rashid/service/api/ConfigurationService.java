package com.rashid.service.api;

import com.rashid.config.AppConfig;
import com.rashid.exception.ConfigException;

// Interface for loading application configuration
public interface ConfigurationService {

    // Loads configuration from properties file and environment variables
    AppConfig load() throws ConfigException;
}
