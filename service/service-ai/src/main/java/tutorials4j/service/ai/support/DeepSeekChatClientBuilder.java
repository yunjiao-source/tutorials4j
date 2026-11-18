package tutorials4j.service.ai.support;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.stereotype.Component;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Component
public class DeepSeekChatClientBuilder extends AbstractChatClientBuilder {
  private final DeepSeekChatModel deepSeekChatModel;

  public DeepSeekChatClientBuilder(
      ChatClientBuilderCreater chatClientBuilderCreater, DeepSeekChatModel deepSeekChatModel) {
    super(chatClientBuilderCreater);
    this.deepSeekChatModel = deepSeekChatModel;
  }

  @Override
  protected ChatModel getChatModel() {
    return deepSeekChatModel;
  }

  @Override
  public ModelType getModelType() {
    return ModelType.deepseek;
  }
}
