package tutorials4j.service.ai.controller;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.service.ai.support.ChatClientBuilderProvider;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("default-system-text")
@RequiredArgsConstructor
public class DefaultSystemTextController implements InitializingBean {
  private final ChatClientBuilderProvider provider;

  private ChatClient chatClient;

  @GetMapping("simple")
  public Map<String, String> completion(
      @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
    String content = chatClient.prompt().user(message).call().content();
    return Map.of("completion", content);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    chatClient =
        provider
            .getBuilderDefault()
            .create()
            .defaultSystem(
                "You are a friendly chat bot that answers question in the voice of a Pirate")
            .build();
  }
}
