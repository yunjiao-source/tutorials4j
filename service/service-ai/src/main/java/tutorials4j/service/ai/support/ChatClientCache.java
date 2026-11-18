package tutorials4j.service.ai.support;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Component
@RequiredArgsConstructor
public class ChatClientCache {
  private static final String DEFAULT_CHART_CLIENT_NAME = "DEFAULT_CHART_CLIENT_NAME";

  private final ChatClientBuilderProvider provider;
  private final ConcurrentMap<String, ChatClient> chatClientConcurrentMap =
      new ConcurrentHashMap<>();

  public ChatClient getDefaultChatClient() {
    return getDefaultChatClient(DEFAULT_CHART_CLIENT_NAME, ChatClientBuilderCustomizer.NOOP);
  }

  public ChatClient getDefaultChatClient(
      String chatClientName, ChatClientBuilderCustomizer chatClientBuilderCustomizer) {
    return chatClientConcurrentMap.computeIfAbsent(
        chatClientName,
        k -> createChatClient(provider.getBuilderDefault().create(), chatClientBuilderCustomizer));
  }

  public ChatClient getDefaultChatClient(ChatClientBuilderCustomizer chatClientBuilderCustomizer) {
    return getDefaultChatClient(DEFAULT_CHART_CLIENT_NAME, chatClientBuilderCustomizer);
  }

  public ChatClient getChatClient(
      ModelType modelType,
      String chatClientName,
      ChatClientBuilderCustomizer chatClientBuilderCustomizer) {
    Assert.notNull(modelType, "modelType must not be null");
    Assert.hasText(chatClientName, "chatClientName must not be null or empty");
    Assert.notNull(chatClientBuilderCustomizer, "chatClientBuilderCustomizer must not be null");

    return chatClientConcurrentMap.computeIfAbsent(
        chatClientName,
        k ->
            createChatClient(provider.getBuilder(modelType).create(), chatClientBuilderCustomizer));
  }

  private ChatClient createChatClient(
      ChatClient.Builder builder, ChatClientBuilderCustomizer chatClientBuilderCustomizer) {
    chatClientBuilderCustomizer.customize(builder);
    return builder.build();
  }
}
