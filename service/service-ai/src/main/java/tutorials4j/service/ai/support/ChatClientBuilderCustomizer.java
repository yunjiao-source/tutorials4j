package tutorials4j.service.ai.support;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClient.Builder;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@FunctionalInterface
public interface ChatClientBuilderCustomizer {
  void customize(ChatClient.Builder builder);

  ChatClientBuilderCustomizer NOOP =
      new ChatClientBuilderCustomizer() {
        @Override
        public void customize(Builder builder) {}
      };
}
