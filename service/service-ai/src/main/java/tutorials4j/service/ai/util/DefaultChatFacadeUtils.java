package tutorials4j.service.ai.util;

import java.util.function.Consumer;
import lombok.Setter;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.StructuredOutputValidationAdvisor;
import org.springframework.ai.chat.client.advisor.ToolCallingAdvisor;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.client.advisor.toolsearch.ToolSearchToolCallingAdvisor;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.StructuredOutputConverter;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.session.SessionService;
import org.springframework.ai.session.advisor.SessionAdvisors;
import org.springframework.ai.session.compaction.TurnCountTrigger;
import org.springframework.ai.session.compaction.WindowCompactionStrategy;
import org.springframework.ai.tool.toolsearch.ToolIndex;
import tutorials4j.service.ai.advisors.MyAuditingToolCallingAdvisor;
import tutorials4j.service.ai.advisors.TokenUsageLoggingAdvisor;
import tutorials4j.service.ai.advisors.ToolCallObservingAdvisor;
import tutorials4j.service.ai.model.ActorsFilms;
import tutorials4j.service.ai.service.AuditService;
import tutorials4j.service.ai.support.ChatFacade;
import tutorials4j.service.ai.support.ChatFacadeCache;
import tutorials4j.service.ai.support.ChatMemoryProvider;
import tutorials4j.service.ai.tools.DemoSearchTools;
import tutorials4j.service.ai.tools.WeatherTools1;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Setter
public class DefaultChatFacadeUtils {
  public static final DefaultChatFacadeUtils instance = new DefaultChatFacadeUtils();
  private ChatFacadeCache chatFacadeCache;
  private ChatMemoryProvider chatMemoryProvider;
  private SessionService sessionService;
  private ToolCallingManager toolCallingManager;
  private AuditService auditService;
  private ToolIndex toolIndex;

  public ChatFacade chatMemoryjdbc() {
    return chatFacadeCache.getDefault(
        "chat-memory-jdbc",
        builder -> {
          builder.defaultAdvisors(
              MessageChatMemoryAdvisor.builder(chatMemoryProvider.jdbc()).build());
        });
  }

  public ChatFacade chatMemoryRedis() {
    return chatFacadeCache.getDefault(
        "chat-memory-redis",
        builder -> {
          builder.defaultAdvisors(
              MessageChatMemoryAdvisor.builder(chatMemoryProvider.redis()).build());
        });
  }

  public ChatFacade defualtSystemText() {
    return chatFacadeCache.getDefault(
        "default-system-text",
        builder -> {
          builder.defaultSystem(
              "You are a friendly chat bot that answers question in the voice of a Pirate");
        });
  }

  public ChatFacade defaultSystemTextWithParameters() {
    return chatFacadeCache.getDefault(
        "default-system-text-with-parameters",
        builder -> {
          builder.defaultSystem(
              "You are a friendly chat bot that answers question in the voice of a {voice}");
        });
  }

  public ChatFacade responseMetadata() {
    return chatFacadeCache.getDefault(
        "response-metadata",
        builder -> {
          builder.defaultAdvisors(new TokenUsageLoggingAdvisor());
        });
  }

  public ChatFacade customizAdvisor() {
    return chatFacadeCache.getDefault(
        "customizing-the-advisor",
        builder -> {
          // 自动生成对应的 JSON Schema
          StructuredOutputConverter<ActorsFilms> myConverter =
              new BeanOutputConverter<>(ActorsFilms.class);

          // 手动构建 Advisor
          var validationAdvisor =
              StructuredOutputValidationAdvisor.builder()
                  // .outputType(ActorsFilms.class) // 指定期望的输出类型，Schema会自动生成
                  .outputJsonSchema(
                      myConverter.getJsonSchema()) // 直接传入预置的Schema字符串, 与 outputType 的互斥关系
                  .maxRepeatAttempts(2) // 自定义最大重试次数
                  // .objectMapper(myCustomObjectMapper) // 传入自定义的 ObjectMapper
                  .build();

          builder.defaultAdvisors(validationAdvisor);
        });
  }

  public ChatFacade session() {
    return chatFacadeCache.getDefault(
        "session",
        builder -> {
          builder.defaultAdvisors(
              SessionAdvisors.builder(sessionService)
                  // 历史超过 20 轮时触发压缩
                  .compaction(
                      new TurnCountTrigger(20),
                      // 始终保留最近 10 个完整轮次
                      WindowCompactionStrategy.builder().lastTurns(10).build())
                  .build());
        });
  }

  // 会话 + 工具调用 + 审计 + observer
  public ChatFacade sessionToolAudit() {

    MyAuditingToolCallingAdvisor myAuditingAdvisor =
        MyAuditingToolCallingAdvisor.builder()
            .toolCallingManager(toolCallingManager) // 必需，用于实际执行工具
            .audit(auditService) // 注入审计服务
            .advisorOrder(BaseAdvisor.HIGHEST_PRECEDENCE + 300) // 建议与默认顺序一致
            .build();

    // 创建你的观察者 Advisor，传入具体的 Consumer 逻辑
    Consumer<ChatClientResponse> observerLogic =
        response -> {
          // 在这里处理你观察到的每一个响应/数据块
          // 例如：打印工具调用信息、记录日志、推送到前端等
          Utils.greenSystemOut("Observed response: " + response);
        };
    ToolCallObservingAdvisor observingAdvisor = new ToolCallObservingAdvisor(observerLogic);

    return chatFacadeCache.getDefault(
        "session_tool_audit",
        builder -> {
          builder
              .defaultTools(new WeatherTools1())
              .defaultAdvisors(myAuditingAdvisor, observingAdvisor)
              .defaultAdvisors(
                  SessionAdvisors.builder(sessionService)
                      // 历史超过 20 轮时触发压缩
                      .compaction(
                          new TurnCountTrigger(20),
                          // 始终保留最近 10 个完整轮次
                          WindowCompactionStrategy.builder().lastTurns(10).build())
                      .build());
        });
  }

  // 工具调用
  public ChatFacade tools() {
    return chatFacadeCache.getDefault(
        "conversation_History_behavior",
        builder -> {
          builder
              // 注册工具，ChatClient 会自动处理工具定义与循环
              .defaultTools(new WeatherTools1())
              // 显式构建 ToolCallingAdvisor，并使用默认的 ToolCallingManager
              .defaultAdvisors(
                  ToolCallingAdvisor.builder()
                      // 调整顺序，确保 ToolCallingAdvisor 在内存 Advisor 内部（默认行为）
                      .advisorOrder(BaseAdvisor.HIGHEST_PRECEDENCE + 300)
                      .build())
              .build();
        });
  }

  public ChatFacade toolSearch() {
    var toolSearchAdvisor =
        ToolSearchToolCallingAdvisor.builder().toolIndex(toolIndex).maxResults(5).build();

    return chatFacadeCache.getDefault(
        "tool-search",
        builder -> {
          builder
              .defaultTools(new DemoSearchTools())

              // add, getCurrentTime没有调用
              .defaultAdvisors(toolSearchAdvisor)

              // add没有调用
              //              .defaultAdvisors(
              //                ToolCallingAdvisor.builder()
              //                    .advisorOrder(BaseAdvisor.HIGHEST_PRECEDENCE + 300)
              //                    .build())
              .build();
        });
  }
}
