package tutorials4j.service.ai.snippet;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Service
@RequiredArgsConstructor
public class ReturningAnEntity {
  private final ChatClient chatClient;

  /** map the AI model’s output to this record using the entity() method */
  public ActorFilms entity() {
    return chatClient
        .prompt()
        .user("Generate the filmography for a random actor.")
        .call()
        .entity(ActorFilms.class);
  }

  /** specify types such as generic Lists */
  public List<ActorFilms> entities() {
    return chatClient
        .prompt()
        .user("Generate the filmography of 5 movies for Tom Hanks and Bill Murray.")
        .call()
        .entity(new ParameterizedTypeReference<List<ActorFilms>>() {});
  }

  /**
   * Reliability Switches: EntityParamSpec
   *
   * <p>所有 {@code entity()} 重载都接受一个可选的 {@code Consumer<EntityParamSpec>}，用于启用两种相互独立且可组合的行为：
   *
   * <ul>
   *   <li>{@code validateSchema()} — 根据实体 schema 验证 JSON 响应， 并在验证失败时自动携带错误反馈进行重试。
   *   <li>{@code useProviderStructuredOutput()} — 将 schema 作为 API 级约束 发送给提供商，而不是作为提示词文本发送。
   * </ul>
   */
  public ActorFilms entityParamSpec() {
    return chatClient
        .prompt()
        .user("Generate the filmography for a random actor.")
        .call()
        .entity(ActorFilms.class, spec -> spec.useProviderStructuredOutput().validateSchema());
  }
}
