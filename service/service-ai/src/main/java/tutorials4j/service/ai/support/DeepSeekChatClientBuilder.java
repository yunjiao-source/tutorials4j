package tutorials4j.service.ai.support;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient.Builder;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.stereotype.Component;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Component
@RequiredArgsConstructor
public class DeepSeekChatClientBuilder implements ChatClientBuilder {
  private final DeepSeekChatModel deepSeekChatModel;
  private final ChatClientBuilderCreater chatClientBuilderCreater;

  private Builder builder;

  @Override
  public Builder create() {
    if (builder != null) {
      return builder.clone();
    }

    synchronized (this) {
      if (builder == null) {
        builder = chatClientBuilderCreater.create(deepSeekChatModel);
      }
    }
    return builder.clone();
  }

  @Override
  public ModelType getModelType() {
    return ModelType.deepseek;
  }
}
