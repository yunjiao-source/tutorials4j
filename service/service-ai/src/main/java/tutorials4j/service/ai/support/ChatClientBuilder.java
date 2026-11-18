package tutorials4j.service.ai.support;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface ChatClientBuilder {
  ChatClient.Builder create();

  ModelType getModelType();

  ChatModel getChatModel();
}
