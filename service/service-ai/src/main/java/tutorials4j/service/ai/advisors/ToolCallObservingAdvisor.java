package tutorials4j.service.ai.advisors;

import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.core.Ordered;
import reactor.core.publisher.Flux;
import tutorials4j.service.ai.util.Utils;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class ToolCallObservingAdvisor implements CallAdvisor, StreamAdvisor {

  private final Consumer<ChatClientResponse> observer;

  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE + 400; // inside ToolCallingAdvisor (order 300)
  }

  @Override
  public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {
    // Each iteration's request includes ToolResponseMessages from prior iterations
    request.prompt().getInstructions().forEach(msg -> Utils.greenSystemOut("Message: " + msg));
    ChatClientResponse response = chain.nextCall(request);
    observer.accept(response);
    return response;
  }

  @Override
  public Flux<ChatClientResponse> adviseStream(
      ChatClientRequest request, StreamAdvisorChain chain) {
    // Observe every chunk including tool-call request chunks
    return chain.nextStream(request).doOnNext(observer);
  }

  @Override
  public String getName() {
    return this.getClass().getSimpleName();
  }
}
