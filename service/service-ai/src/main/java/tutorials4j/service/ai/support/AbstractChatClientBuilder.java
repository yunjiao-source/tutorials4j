package tutorials4j.service.ai.support;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient.Builder;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public abstract class AbstractChatClientBuilder implements ChatClientBuilder {
  private final ChatClientBuilderCreater chatClientBuilderCreater;

  private Builder builder;

  @Override
  public Builder create() {
    if (builder != null) {
      return builder.clone();
    }

    synchronized (this) {
      if (builder == null) {
        builder = chatClientBuilderCreater.create(getChatModel());
        builder.defaultAdvisors(
            new SimpleLoggerAdvisor(
                //                request -> "Custom request: " + request.prompt().getUserMessage(),
                //                response -> "Custom response: " + response.getResult(),
                //                0
                ));
      }
    }
    return builder.clone();
  }
}
