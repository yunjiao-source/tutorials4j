package tutorials4j.service.ai.controller;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.AdvisorParams;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.service.ai.support.ChatFacade;
import tutorials4j.service.ai.tools.DateTimeTools;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("tool")
@RequiredArgsConstructor
public class ToolAiController {
  private final ChatFacade chatFacade;

  @GetMapping("tool-calling")
  public Map<String, String> toolCalling() {

    String content =
        chatFacade
            .client()
            .prompt("What day is tomorrow?")
            .tools(new DateTimeTools())
            .call()
            .content();
    return Map.of("completion", content);
  }

  @GetMapping("per-tool-calling")
  public Map<String, String> perToolCalling() {

    String content =
        chatFacade
            .client()
            .prompt("What day is tomorrow?")
            .tools(new DateTimeTools())
            .advisors(AdvisorParams.toolCallingAdvisorAutoRegister(false))
            .call()
            .content();
    return Map.of("completion", content);
  }
}
