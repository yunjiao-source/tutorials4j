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
public class ChatClientCache {
  private static final String DEFAULT_CHART_CLIENT_NAME = "DEFAULT_CHART_CLIENT_NAME";
  private final AIServiceProperties properties;
  private final ChatClientBuilderProvider provider;
  private final ConcurrentMap<String, ChatClient> chatClientConcurrentMap =
      new ConcurrentHashMap<>();

  public ChatClient getDefault() {
    return getDefault(DEFAULT_CHART_CLIENT_NAME, ChatClientBuilderCustomizer.NOOP);
  }

  public ChatClient getDefault(
      String chatClientName, ChatClientBuilderCustomizer chatClientBuilderCustomizer) {
    ModelType defaultModelType = properties.getDefaultModelType();

    return getChatClient(defaultModelType, chatClientName, chatClientBuilderCustomizer);
  }

  public ChatClient getDefault(ChatClientBuilderCustomizer chatClientBuilderCustomizer) {
    return getDefault(DEFAULT_CHART_CLIENT_NAME, chatClientBuilderCustomizer);
  }

  public ChatClient getOllama() {
    return getOllama(DEFAULT_CHART_CLIENT_NAME, ChatClientBuilderCustomizer.NOOP);
  }

  public ChatClient getOllama(
      String chatClientName, ChatClientBuilderCustomizer chatClientBuilderCustomizer) {
    return getChatClient(ModelType.ollama, chatClientName, chatClientBuilderCustomizer);
  }

  public ChatClient getOllama(ChatClientBuilderCustomizer chatClientBuilderCustomizer) {
    return getOllama(DEFAULT_CHART_CLIENT_NAME, chatClientBuilderCustomizer);
  }

  protected ChatClient getChatClient(
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
          return createChatClient(
              provider.getBuilder(modelType).create(), chatClientBuilderCustomizer);
        });
  }

  private ChatClient createChatClient(
      ChatClient.Builder builder, ChatClientBuilderCustomizer chatClientBuilderCustomizer) {
    chatClientBuilderCustomizer.customize(builder);
    return builder.build();
  }

  private String wrapperName(String chatClientName, ModelType modelType) {
    return chatClientName.toUpperCase() + "_" + modelType.name().toUpperCase();
  }
}
