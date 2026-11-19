package tutorials4j.service.ai.controller;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.service.ai.support.ChatFacade;
import tutorials4j.service.ai.support.ChatFacadeCache;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("ollama")
@RequiredArgsConstructor
public class OllamaAiController {
  private final ChatFacadeCache chatFacadeCache;

  @GetMapping("simple")
  public Map<String, String> simple() {
    ChatFacade chatFacade = chatFacadeCache.getOllama();
    String content = chatFacade.client().prompt("Tell me a joke").call().content();
    return Map.of("completion", content);
  }
}
