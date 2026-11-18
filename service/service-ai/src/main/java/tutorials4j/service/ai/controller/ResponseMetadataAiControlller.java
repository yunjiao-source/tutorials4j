package tutorials4j.service.ai.controller;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.service.ai.advisors.TokenUsageLoggingAdvisor;
import tutorials4j.service.ai.support.ChatClientCache;
import tutorials4j.service.ai.util.Utils;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("response-metadata")
@RequiredArgsConstructor
public class ResponseMetadataAiControlller {
  private final ChatClientCache chatClientCache;

  @GetMapping("metadata")
  public Map<String, Usage> metadata() {
    ChatResponse response =
        getChatClient().prompt("What is the weather like today?").call().chatResponse();

    Usage usage = response.getMetadata().getUsage();

    Long cacheReadTokens = usage.getCacheReadInputTokens();
    Long cacheWriteTokens = usage.getCacheWriteInputTokens();

    if (cacheReadTokens != null && cacheReadTokens > 0) {
      System.out.println("Cache hit: " + cacheReadTokens + " tokens read from cache");
    }
    if (cacheWriteTokens != null && cacheWriteTokens > 0) {
      System.out.println("Cache write: " + cacheWriteTokens + " tokens written to cache");
    }
    return Map.of("completion", usage);
  }

  @GetMapping("multi-step-usage")
  public Map<String, String> multiStepUsage() {
    String content =
        Utils.multiStepWorkflow(
            getChatClient(), "Q1 revenue grew 15%, Q2 revenue grew 22, Q3 revenue declined -5%");
    return Map.of("completion", content);
  }

  private ChatClient getChatClient() {
    return chatClientCache.getDefault(
        "response-metadata",
        builder -> {
          builder.defaultAdvisors(new TokenUsageLoggingAdvisor());
        });
  }
}
