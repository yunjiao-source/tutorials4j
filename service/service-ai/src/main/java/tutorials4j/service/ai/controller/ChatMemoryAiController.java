package tutorials4j.service.ai.controller;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.service.ai.util.DefaultChatFacadeUtils;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("chat-memory")
@RequiredArgsConstructor
public class ChatMemoryAiController {
  @GetMapping("/jdbc")
  public Map<String, String> jdbc(String message, String chatId) {
    String content =
        DefaultChatFacadeUtils.instance
            .chatMemoryjdbc()
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
        DefaultChatFacadeUtils.instance
            .chatMemoryRedis()
            .client()
            .prompt()
            .user(message)
            .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
            .call()
            .content();

    return Map.of("completion", content);
  }
}
