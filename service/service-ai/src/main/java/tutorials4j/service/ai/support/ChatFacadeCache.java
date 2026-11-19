package tutorials4j.service.ai.support;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import tutorials4j.service.ai.config.AIServiceProperties;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChatFacadeCache {
  private static final String DEFAULT_CHART_CLIENT_NAME = "DEFAULT_CHART_CLIENT_NAME";
  private final AIServiceProperties properties;
  private final ChatClientBuilderProvider provider;
  private final ConcurrentMap<String, ChatFacade> chatClientConcurrentMap =
      new ConcurrentHashMap<>();

  public ChatFacade getDefault() {
    return getDefault(DEFAULT_CHART_CLIENT_NAME, ChatClientBuilderCustomizer.NOOP);
  }

  public ChatFacade getDefault(String chatClientName) {
    return getDefault(chatClientName, ChatClientBuilderCustomizer.NOOP);
  }

  public ChatFacade getDefault(ChatClientBuilderCustomizer chatClientBuilderCustomizer) {
    return getDefault(DEFAULT_CHART_CLIENT_NAME, chatClientBuilderCustomizer);
  }

  public ChatFacade getDefault(
      String chatClientName, ChatClientBuilderCustomizer chatClientBuilderCustomizer) {
    ModelType defaultModelType = properties.getDefaultModelType();

    return getChatFacade(defaultModelType, chatClientName, chatClientBuilderCustomizer);
  }

  public ChatFacade getOllama() {
    return getOllama(DEFAULT_CHART_CLIENT_NAME, ChatClientBuilderCustomizer.NOOP);
  }

  public ChatFacade getOllama(String chatClientName) {
    return getOllama(chatClientName, ChatClientBuilderCustomizer.NOOP);
  }

  public ChatFacade getOllama(ChatClientBuilderCustomizer chatClientBuilderCustomizer) {
    return getOllama(DEFAULT_CHART_CLIENT_NAME, chatClientBuilderCustomizer);
  }

  public ChatFacade getOllama(
      String chatClientName, ChatClientBuilderCustomizer chatClientBuilderCustomizer) {
    return getChatFacade(ModelType.ollama, chatClientName, chatClientBuilderCustomizer);
  }

  public ChatFacade getOpenai() {
    return getOpenai(DEFAULT_CHART_CLIENT_NAME, ChatClientBuilderCustomizer.NOOP);
  }

  public ChatFacade getOpenai(String chatClientName) {
    return getOpenai(chatClientName, ChatClientBuilderCustomizer.NOOP);
  }

  public ChatFacade getOpenai(ChatClientBuilderCustomizer chatClientBuilderCustomizer) {
    return getOpenai(DEFAULT_CHART_CLIENT_NAME, chatClientBuilderCustomizer);
  }

  public ChatFacade getOpenai(
      String chatClientName, ChatClientBuilderCustomizer chatClientBuilderCustomizer) {
    return getChatFacade(ModelType.openai, chatClientName, chatClientBuilderCustomizer);
  }

  public ChatFacade getDeepSeek() {
    return getOpenai(DEFAULT_CHART_CLIENT_NAME, ChatClientBuilderCustomizer.NOOP);
  }

  public ChatFacade getDeepSeek(String chatClientName) {
    return getDeepSeek(chatClientName, ChatClientBuilderCustomizer.NOOP);
  }

  public ChatFacade getDeepSeek(ChatClientBuilderCustomizer chatClientBuilderCustomizer) {
    return getDeepSeek(DEFAULT_CHART_CLIENT_NAME, chatClientBuilderCustomizer);
  }

  public ChatFacade getDeepSeek(
      String chatClientName, ChatClientBuilderCustomizer chatClientBuilderCustomizer) {
    return getChatFacade(ModelType.deepseek, chatClientName, chatClientBuilderCustomizer);
  }

  protected ChatFacade getChatFacade(
      ModelType modelType,
      String chatClientName,
      ChatClientBuilderCustomizer chatClientBuilderCustomizer) {
    Assert.notNull(modelType, "modelType must not be null");
    Assert.hasText(chatClientName, "chatClientName must not be null or empty");
    Assert.notNull(chatClientBuilderCustomizer, "chatClientBuilderCustomizer must not be null");

    String newChatClientName = wrapperName(chatClientName, modelType);
    return chatClientConcurrentMap.computeIfAbsent(
        newChatClientName,
        k -> {
          if (log.isDebugEnabled()) {
            log.debug("创建新的`ChatClient`实例, name: {}, model type: {}", chatClientName, modelType);
          }
          return createChatFacade(provider.getBuilder(modelType), chatClientBuilderCustomizer);
        });
  }

  private ChatFacade createChatFacade(
      ChatClientBuilder chatClientBuilder,
      ChatClientBuilderCustomizer chatClientBuilderCustomizer) {
    ChatClient.Builder builder = chatClientBuilder.create();
    chatClientBuilderCustomizer.customize(builder);
    ChatClient chatClient = builder.build();
    return ChatFacade.of(chatClient, chatClientBuilder.getChatModel());
  }

  private String wrapperName(String chatClientName, ModelType modelType) {
    return chatClientName.toUpperCase() + "_" + modelType.name().toUpperCase();
  }
}
