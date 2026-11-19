package tutorials4j.service.ai.support;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.util.Assert;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public record ChatFacade(ChatClient client, ChatModel model) {

  public static ChatFacade of(ChatClient chatClient, ChatModel chatModel) {
    Assert.notNull(chatClient, "chatClient must not be null");
    Assert.notNull(chatModel, "chatModel must not be null");
    return new ChatFacade(chatClient, chatModel);
  }
}
