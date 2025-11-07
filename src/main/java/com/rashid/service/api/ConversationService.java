package com.rashid.service.api;

import com.rashid.exception.ResearchException;
import com.rashid.model.ResearchIteration;

import java.util.List;

// Interface for handling follow-up conversations after research
public interface ConversationService {

    // Handles unlimited follow-up conversation after initial research
    void continuousConversation(String topic, String previousFindings,
                                List<ResearchIteration> iterations) throws ResearchException;
}
