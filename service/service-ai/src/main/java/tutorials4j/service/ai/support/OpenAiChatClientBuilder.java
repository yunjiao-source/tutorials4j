package tutorials4j.service.ai.support;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Component;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Component
public class OpenAiChatClientBuilder extends AbstractChatClientBuilder {
  private final OpenAiChatModel openAiChatModel;

  public OpenAiChatClientBuilder(
      ChatClientBuilderCreater chatClientBuilderCreater, OpenAiChatModel openAiChatModel) {
    super(chatClientBuilderCreater);
    this.openAiChatModel = openAiChatModel;
  }

  @Override
  public ChatModel getChatModel() {
    return openAiChatModel;
  }

  @Override
  public ModelType getModelType() {
    return ModelType.openai;
  }
}
