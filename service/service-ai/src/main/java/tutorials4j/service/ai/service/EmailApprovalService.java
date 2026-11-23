package tutorials4j.service.ai.service;

import java.util.List;
import java.util.Scanner;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallDecision;
import org.springframework.ai.model.tool.ToolCallDecisions;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.stereotype.Service;
import tutorials4j.service.ai.support.ChatFacade;
import tutorials4j.service.ai.support.ChatFacadeCache;
import tutorials4j.service.ai.tools.EmailTools;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Service
@RequiredArgsConstructor
public class EmailApprovalService {
  private final ChatFacadeCache chatFacadeCache;
  private final ToolCallingManager toolCallingManager;

  public String chatWithConfirmation(String userInput) {
    // 1) 发起请求，必须拿完整的 ChatResponse，不能只取 content()
    ChatResponse response = getChatFacade().client().prompt().user(userInput).call().chatResponse();

    ToolCallingChatOptions options =
        ToolCallingChatOptions.builder()
            .toolCallbacks(ToolCallbacks.from(new EmailTools())) // 显式绑定工具
            .build();

    // 2) 只要还有待确认的调用，就循环
    while (!ToolCallDecisions.awaitingDecision(response).isEmpty()) {
      List<String> awaitingKeys = ToolCallDecisions.awaitingDecision(response);
      AssistantMessage pausedReply = response.getResult().getOutput();

      // 3) 为整条回复的“每一个”工具调用收集决策
      ToolCallDecisions decisions = collectDecisions(pausedReply, awaitingKeys);

      Prompt promptWithTools = new Prompt(userInput, options);

      // 4) 用原始 Prompt + 暂停的回复 + 决策，执行工具调用
      //    注意：Prompt 需要带上工具定义，否则 manager 无法匹配工具
      ToolExecutionResult result =
          toolCallingManager.executeToolCalls(promptWithTools, response, decisions);

      // 5) 把工具结果送回模型，继续对话
      response =
          getChatFacade()
              .client()
              .prompt()
              .messages(result.conversationHistory())
              .call()
              .chatResponse();
    }

    return response.getResult().getOutput().getText();
  }

  private ToolCallDecisions collectDecisions(
      AssistantMessage pausedReply, List<String> awaitingKeys) {
    var decisions =
        ToolCallDecisions.toolCallKeys(pausedReply).stream()
            .map(
                key -> {
                  if (!awaitingKeys.contains(key)) {
                    // 同一回复中无需确认的调用，直接允许
                    return ToolCallDecision.allow(key);
                  }
                  // 需要确认：展示工具名和参数，征求人工输入
                  var call =
                      pausedReply.getToolCalls().stream()
                          .filter(c -> c.id().equals(key))
                          .findFirst()
                          .orElseThrow();
                  System.out.printf(
                      "%n[待确认] 工具: %s%n参数: %s%n允许? (y/n): ", call.name(), call.arguments());
                  String input = new Scanner(System.in).nextLine().trim();
                  return input.equalsIgnoreCase("y")
                      ? ToolCallDecision.allow(key)
                      : ToolCallDecision.deny(key, "用户未批准此操作。");
                })
            .toList();
    return new ToolCallDecisions(decisions);
  }

  public ChatFacade getChatFacade() {
    return chatFacadeCache.getDefault(
        "email-approval",
        builder -> {
          builder.defaultTools(new EmailTools());
        });
  }
}
