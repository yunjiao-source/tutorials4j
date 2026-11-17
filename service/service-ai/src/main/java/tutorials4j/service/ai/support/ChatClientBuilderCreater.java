package tutorials4j.service.ai.support;

import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.ToolCallingAdvisor;
import org.springframework.ai.chat.client.advisor.observation.AdvisorObservationConvention;
import org.springframework.ai.chat.client.observation.ChatClientObservationConvention;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.model.chat.client.autoconfigure.ChatClientBuilderConfigurer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Component
@RequiredArgsConstructor
public class ChatClientBuilderCreater {
  private final ChatClientBuilderConfigurer configurer;
  private final ObjectProvider<ObservationRegistry> observationRegistry;
  private final ObjectProvider<ChatClientObservationConvention> chatClientObservationConvention;
  private final ObjectProvider<AdvisorObservationConvention> advisorObservationConvention;
  private final ObjectProvider<ToolCallingAdvisor.Builder<?>> toolCallingAdvisorBuilder;

  public ChatClient.Builder create(ChatModel chatModel) {
    ChatClient.Builder builder =
        ChatClient.builder(
            chatModel,
            observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP),
            chatClientObservationConvention.getIfUnique(),
            advisorObservationConvention.getIfUnique(),
            toolCallingAdvisorBuilder.getIfAvailable());
    return configurer.configure(builder);
  }
}
