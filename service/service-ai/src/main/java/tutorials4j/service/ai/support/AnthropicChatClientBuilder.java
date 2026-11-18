package tutorials4j.service.ai.support;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Component
public class AnthropicChatClientBuilder extends AbstractChatClientBuilder {
  private final AnthropicChatModel anthropicChatModel;

  public AnthropicChatClientBuilder(
      ChatClientBuilderCreater chatClientBuilderCreater, AnthropicChatModel anthropicChatModel) {
    super(chatClientBuilderCreater);
    this.anthropicChatModel = anthropicChatModel;
  }

  @Override
  protected ChatModel getChatModel() {
    return anthropicChatModel;
  }

  @Override
  public ModelType getModelType() {
    return ModelType.anthropic;
  }
}
