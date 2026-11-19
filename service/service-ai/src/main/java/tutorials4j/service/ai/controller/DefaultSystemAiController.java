package tutorials4j.service.ai.controller;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.service.ai.support.ChatFacade;
import tutorials4j.service.ai.support.ChatFacadeCache;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("default-system")
@RequiredArgsConstructor
public class DefaultSystemAiController {
  private final ChatFacadeCache chatFacadeCache;

  @GetMapping("default-system-text")
  public Map<String, String> defualtSystemText(
      @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
    ChatFacade chatFacade = getDefualtSystemTextChatFacade();
    String content = chatFacade.client().prompt().user(message).call().content();
    return Map.of("completion", content);
  }

  @GetMapping("default-system-text-with-parameters")
  public Map<String, String> defaultSystemTextWithParameters(
      @RequestParam(value = "message", defaultValue = "Tell me a joke") String message,
      String voice) {
    ChatFacade chatFacade = getDefaultSystemTextWithParametersChatFacade();
    String content =
        chatFacade
            .client()
            .prompt()
            .system(sp -> sp.param("voice", voice))
            .user(message)
            .call()
            .content();
    return Map.of("completion", content);
  }

  private ChatFacade getDefaultSystemTextWithParametersChatFacade() {
    return chatFacadeCache.getDefault(
        "default-system-text-with-parameters",
        builder -> {
          builder.defaultSystem(
              "You are a friendly chat bot that answers question in the voice of a {voice}");
        });
  }

  private ChatFacade getDefualtSystemTextChatFacade() {
    return chatFacadeCache.getDefault(
        "default-system-text",
        builder -> {
          builder.defaultSystem(
              "You are a friendly chat bot that answers question in the voice of a Pirate");
        });
  }
}
