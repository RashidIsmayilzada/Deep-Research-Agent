package com.rashid.service.api;

import com.rashid.exception.ResearchException;

// Interface for clarifying user research needs through conversation
public interface UserClarificationService {

    // AI-driven conversation to clarify what user wants
    String clarifyThroughConversation(String topic) throws ResearchException;
}
