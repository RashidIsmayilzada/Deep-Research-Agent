package com.rashid.constants;

// Constants for prompt prefixes and markers used in AI responses
public final class PromptConstants {

    private PromptConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }

    // Prefixes used to identify specific content in AI responses
    public static final String GAP_PREFIX = "GAP:";
    public static final String QUERY_PREFIX = "QUERY:";
    public static final String READY_PREFIX = "READY:";

    // Decision keywords from AI
    public static final String DECISION_CONTINUE = "CONTINUE";

    // OpenAI API role constants
    public static final String ROLE_SYSTEM = "system";
    public static final String ROLE_USER = "user";

    // Exit commands for conversation
    public static final String EXIT_DONE = "done";
    public static final String EXIT_QUIT = "quit";
    public static final String EXIT_EXIT = "exit";
}
