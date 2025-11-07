package com.rashid.service.research;

import com.rashid.exception.ResearchException;
import com.rashid.model.ResearchIteration;
import com.rashid.service.api.AIClient;

import java.util.List;

// Service for synthesizing research findings into comprehensive reports
public class SynthesisService {

    private final AIClient aiClient;

    public SynthesisService(AIClient aiClient) {
        this.aiClient = aiClient;
    }

    // Synthesizes findings from all iterations into a comprehensive report
    public String synthesizeAllIterations(String topic, List<ResearchIteration> iterations) throws ResearchException {
        StringBuilder allFindings = new StringBuilder();
        for (ResearchIteration iteration : iterations) {
            allFindings.append("=== Iteration ").append(iteration.getIterationNumber()).append(" ===\n");
            allFindings.append(iteration.getFindings()).append("\n\n");
        }

        String synthesisPrompt = String.format(
            "Synthesize the following research findings on '%s' into a comprehensive, well-structured report.\n\n" +
            "Research findings from %d iterations:\n%s\n\n" +
            "Create a cohesive synthesis that:\n" +
            "1. Integrates findings from all iterations\n" +
            "2. Removes redundancy while preserving unique insights\n" +
            "3. Presents a complete, authoritative overview\n" +
            "4. Highlights areas where sources agree or disagree (contradictions)\n" +
            "5. Organizes information logically with clear sections\n" +
            "6. Addresses all identified gaps with discovered information\n\n" +
            "IMPORTANT: Do NOT include a separate 'Citations' or 'References' section at the end. " +
            "Sources will be displayed automatically. Focus only on presenting the research findings and insights.\n\n" +
            "The final report should read as a unified, comprehensive research document.",
            topic, iterations.size(), allFindings.toString()
        );

        return aiClient.chat(synthesisPrompt);
    }

    // Extracts key findings from the final synthesis
    public String extractKeyFindings(String topic, String synthesis) throws ResearchException {
        String findingsPrompt = String.format(
            "Based on this comprehensive research about %s, extract 7-10 key findings.\n\n" +
            "Research:\n%s\n\n" +
            "Include:\n" +
            "- Latest statistics and data points\n" +
            "- Important conclusions and insights\n" +
            "- Expert consensus or debates\n" +
            "- Significant trends and patterns\n" +
            "- Critical implications\n" +
            "- Any contradictions or ongoing debates\n\n" +
            "Format as concise, impactful bullet points with specific numbers, dates, and facts.\n\n" +
            "IMPORTANT: Do NOT include a 'Citations' or 'References' section. Sources are displayed separately.",
            topic, synthesis
        );

        return aiClient.chat(findingsPrompt);
    }
}
