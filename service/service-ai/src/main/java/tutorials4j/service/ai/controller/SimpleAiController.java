package tutorials4j.service.ai.controller;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.service.ai.support.ChatClientCache;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("ai")
@RequiredArgsConstructor
public class SimpleAiController {
  private final ChatClientCache chatClientCache;

  @GetMapping("default-system-text")
  public Map<String, String> completion(
      @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
    ChatClient chatClient = getDefualtSystemTextChatClient();
    String content = chatClient.prompt().user(message).call().content();
    return Map.of("completion", content);
  }

  @GetMapping("default-system-text-with-parameters")
  public Map<String, String> completion(
      @RequestParam(value = "message", defaultValue = "Tell me a joke") String message,
      String voice) {
    ChatClient chatClient = getDefaultSystemTextWithParametersChatClient();
    String content =
        chatClient.prompt().system(sp -> sp.param("voice", voice)).user(message).call().content();
    return Map.of("completion", content);
  }

  private ChatClient getDefaultSystemTextWithParametersChatClient() {
    return chatClientCache.getDefaultChatClient(
        "default-system-text-with-parameters",
        builder -> {
          builder.defaultSystem(
              "You are a friendly chat bot that answers question in the voice of a {voice}");
        });
  }

  private ChatClient getDefualtSystemTextChatClient() {
    return chatClientCache.getDefaultChatClient(
        "default-system-text",
        builder -> {
          builder.defaultSystem(
              "You are a friendly chat bot that answers question in the voice of a Pirate");
        });
  }
}
