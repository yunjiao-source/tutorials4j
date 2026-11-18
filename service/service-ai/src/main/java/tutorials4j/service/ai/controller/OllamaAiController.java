package tutorials4j.service.ai.controller;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.service.ai.support.ChatClientCache;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("ollama")
@RequiredArgsConstructor
public class OllamaAiController {
  private final ChatClientCache chatClientCache;

  @GetMapping("simple")
  public Map<String, String> simple() {
    ChatClient chatClient = chatClientCache.getOllama();
    String content = chatClient.prompt("Tell me a joke").call().content();
    return Map.of("completion", content);
  }
}
