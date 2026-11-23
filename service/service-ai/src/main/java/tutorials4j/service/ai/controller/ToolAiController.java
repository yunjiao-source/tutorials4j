package tutorials4j.service.ai.controller;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.AdvisorParams;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.MessageAggregator;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.session.advisor.SessionAdvisors;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;
import tutorials4j.service.ai.service.EmailApprovalService;
import tutorials4j.service.ai.support.ChatFacade;
import tutorials4j.service.ai.tools.DateTimeTools;
import tutorials4j.service.ai.tools.WeatherTools;
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

  @GetMapping("/chatmodel/per-request-Tools")
  public Map<String, String> perRequestTools() {
    ToolCallback[] tools = ToolCallbacks.from(new DateTimeTools());

    ChatOptions chatOptions = DeepSeekChatOptions.builder().toolCallbacks(tools).build();

    Prompt prompt = new Prompt("What day is tomorrow?", chatOptions);
    String content = chatFacade.model().call(prompt).getResult().getOutput().getText();
    return Map.of("completion", content);
  }

  @GetMapping("/chatmodel/blocking")
  public Map<String, Object> blocking() {
    ToolCallingManager toolCallingManager = ToolCallingManager.builder().build();

    ToolCallback[] tools = ToolCallbacks.from(new WeatherTools());
    ChatOptions chatOptions = DeepSeekChatOptions.builder().toolCallbacks(tools).build();

    Prompt prompt = new Prompt("What is the weather in Amsterdam and Paris ?", chatOptions);
    ChatResponse response = chatFacade.model().call(prompt);

    while (response.hasToolCalls()) {
      ToolExecutionResult result = toolCallingManager.executeToolCalls(prompt, response);

      if (result.returnDirect()) {
        // Tool's returnDirect=true — break out without sending back to the model
        return Map.of("completion", result.conversationHistory());
      }

      prompt = new Prompt(result.conversationHistory(), chatOptions);
      response = chatFacade.model().call(prompt);
    }

    String content = response.getResult().getOutput().getText();
    return Map.of("completion", content);
  }

  @GetMapping("/chatmodel/stream")
  public Flux<String> stream() {
    return streamWithToolCalls().map(cr -> cr.getResult().getOutput().getText());
  }

  public Flux<ChatResponse> streamWithToolCalls() {
    return Flux.<ChatResponse>create(
            sink -> {
              try {
                ToolCallingManager toolCallingManager = ToolCallingManager.builder().build();

                ToolCallback[] tools = ToolCallbacks.from(new WeatherTools());
                ChatOptions chatOptions =
                    DeepSeekChatOptions.builder().toolCallbacks(tools).build();

                Prompt prompt =
                    new Prompt("What is the weather in Amsterdam and Paris?", chatOptions);

                while (true) {
                  AtomicReference<ChatResponse> aggregated = new AtomicReference<>();

                  // 每个 chunk 实时向下游发送（sink::next），同时聚合整轮结果
                  new MessageAggregator()
                      .aggregate(
                          chatFacade.model().stream(prompt).doOnNext(sink::next), aggregated::set)
                      .blockLast();

                  ChatResponse response = aggregated.get();
                  if (response == null || !response.hasToolCalls()) {
                    break;
                  }

                  ToolExecutionResult result =
                      toolCallingManager.executeToolCalls(prompt, response);
                  if (result.returnDirect()) {
                    break;
                  }

                  prompt = new Prompt(result.conversationHistory(), chatOptions);
                }

                sink.complete();
              } catch (Throwable t) {
                sink.error(t);
              }
            },
            FluxSink.OverflowStrategy.BUFFER)
        .subscribeOn(Schedulers.boundedElastic()); // 把阻塞的 blockLast 放到 boundedElastic 线程池
  }
}
