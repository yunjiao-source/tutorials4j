package tutorials4j.service.ai.tools;

import org.springframework.ai.tool.annotation.ToolParam;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public record AgentThinking(
    @ToolParam(description = "Your reasoning for calling this tool", required = true)
        String innerThought,
    @ToolParam(description = "Confidence level (low, medium, high)", required = false)
        String confidence) {}
