package tutorials4j.service.ai.controller;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.service.ai.support.ChatFacade;
import tutorials4j.service.ai.support.ChatFacadeCache;
import tutorials4j.service.ai.support.ChatMemoryProvider;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("chat-memory")
@RequiredArgsConstructor
public class ChatMemoryAiController {
  private final ChatFacadeCache chatFacadeCache;
  private final ChatMemoryProvider chatMemoryProvider;

  @GetMapping("/jdbc")
  public Map<String, String> jdbc(String message, String chatId) {
    String content =
        getJdbcMemoryChatFacade()
            .client()
            .prompt()
            .user(message)
            .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
            .call()
            .content();

    return Map.of("completion", content);
  }

  @GetMapping("/redis")
  public Map<String, String> redis(String message, String chatId) {
    String content =
        getRedisMemoryChatFacade()
            .client()
            .prompt()
            .user(message)
            .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
            .call()
            .content();

    return Map.of("completion", content);
  }

  private ChatFacade getJdbcMemoryChatFacade() {
    return chatFacadeCache.getDefault(
        "chat-memory-jdbc",
        builder -> {
          builder.defaultAdvisors(
              MessageChatMemoryAdvisor.builder(chatMemoryProvider.jdbc()).build());
        });
  }

  private ChatFacade getRedisMemoryChatFacade() {
    return chatFacadeCache.getDefault(
        "chat-memory-redis",
        builder -> {
          builder.defaultAdvisors(
              MessageChatMemoryAdvisor.builder(chatMemoryProvider.redis()).build());
        });
  }
}
