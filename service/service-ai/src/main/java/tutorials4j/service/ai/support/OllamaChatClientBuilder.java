package tutorials4j.service.ai.support;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Component;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Component
public class OllamaChatClientBuilder extends AbstractChatClientBuilder {
  private final OllamaChatModel ollamaChatModel;

  public OllamaChatClientBuilder(
      ChatClientBuilderCreater chatClientBuilderCreater, OllamaChatModel ollamaChatModel) {
    super(chatClientBuilderCreater);
    this.ollamaChatModel = ollamaChatModel;
  }

  @Override
  public ChatModel getChatModel() {
    return ollamaChatModel;
  }

  @Override
  public ModelType getModelType() {
    return ModelType.ollama;
  }
}
