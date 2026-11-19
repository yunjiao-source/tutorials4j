package tutorials4j.service.ai.controller;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.service.ai.support.ChatFacade;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("prompt-engineering")
@RequiredArgsConstructor
public class PromptEngineeringAiController {
  private final ChatFacade chatFacade;

  // 清晰的任务指令是关键，这里利用了 {review} 占位符来注入动态内容
  @GetMapping("zero-shot-prompting")
  public Map<String, String> zeroShotPrompting() {

    String content =
        chatFacade
            .client()
            .prompt()
            .user(
                u ->
                    u.text("Classify the sentiment of this review: \"{review}\"")
                        .param("review", "The product quality is really great!"))
            .call()
            .content();

    return Map.of("completion", content);
  }

  // 示例（sea otter, peppermint）教会了模型“翻译”任务的精确格式，模型会模仿这个模式处理新的输入 cheese
  @GetMapping("few-shot-prompting")
  public Map<String, String> fewShotPrompting() {

    String promptText =
        """
    Translate English to French:
    sea otter => loutre de mer
    peppermint => menthe poivrée
    {input} =>
    """;

    String content =
        chatFacade
            .client()
            .prompt()
            .user(u -> u.text(promptText).param("input", "cheese"))
            .call()
            .content();

    return Map.of("completion", content);
  }

  // system 指令让模型的回答会自然地向“架构师”的视角和风格倾斜，无需每次都在用户问题中重复这些要求
  @GetMapping("system-prompting")
  public Map<String, String> systemPrompting() {

    String systemInstruction =
        "You are a senior Java architect. Answer questions with a focus on design patterns, scalability, and code examples. Be concise.";
    String userQuestion = "How should I structure a new Spring Boot microservice?";

    String content =
        chatFacade
            .client()
            .prompt()
            .system(systemInstruction) // 设定角色和回答风格
            .user(userQuestion)
            .call()
            .content();

    return Map.of("completion", content);
  }
}
