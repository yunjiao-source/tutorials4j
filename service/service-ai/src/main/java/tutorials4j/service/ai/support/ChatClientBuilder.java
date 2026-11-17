package tutorials4j.service.ai.support;

import org.springframework.ai.chat.client.ChatClient;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface ChatClientBuilder {
  ChatClient.Builder create();

  ModelType getModelType();
}
