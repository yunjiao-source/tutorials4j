package tutorials4j.service.ai.controller;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.AdvisorParams;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.session.advisor.SessionAdvisors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.service.ai.service.EmailApprovalService;
import tutorials4j.service.ai.support.ChatFacade;
import tutorials4j.service.ai.tools.DateTimeTools;
import tutorials4j.service.ai.util.DefaultChatFacadeUtils;

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
  private final EmailApprovalService emailApprovalService;

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

  @GetMapping("quick-start")
  public Map<String, String> quickStart() {

    String content =
        chatFacade
            .client()
            .prompt("Can you set an alarm 10 minutes from now?")
            .tools(new DateTimeTools())
            .call()
            .content();
    return Map.of("completion", content);
  }

  @GetMapping("/weather/ask")
  public Map<String, String> weatherAsk(
      @RequestParam String question, @RequestParam String chatId) {
    String content =
        DefaultChatFacadeUtils.instance
            .tools()
            .client()
            .prompt()
            .user(question)
            .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
            .call()
            .content();

    return Map.of("completion", content);
  }

  @GetMapping("/{sessionId}/weather/ask")
  public Map<String, String> sessionWeatherAsk(
      @PathVariable String sessionId, @RequestParam String question) {
    String content =
        DefaultChatFacadeUtils.instance
            .sessionToolAudit()
            .client()
            .prompt()
            .user(question)
            .advisors(SessionAdvisors.session(sessionId))
            .call()
            .content();

    return Map.of("completion", content);
  }

  @GetMapping("/search")
  public Map<String, String> search() {

    String content =
        DefaultChatFacadeUtils.instance
            .toolSearch()
            .client()
            .prompt()
            .user("帮我看看今天在 成都 穿什么，顺便告诉我天气和附近有哪些服装店。")
            .call()
            .content();
    return Map.of("completion", content);
  }

  @PostMapping("/tool-call-confirmation")
  public Map<String, String> toolCallConfirmation(@RequestBody String message) {
    String content = emailApprovalService.chatWithConfirmation(message);
    return Map.of("completion", content);
  }
}
