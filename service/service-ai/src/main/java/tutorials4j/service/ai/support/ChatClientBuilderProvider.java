package tutorials4j.service.ai.support;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tutorials4j.service.ai.config.AIServiceProperties;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChatClientBuilderProvider {
  private final AIServiceProperties properties;
  private final List<ChatClientBuilder> builders;

  @PostConstruct
  public void postConstruct() {
    log.trace("[SERVICE-AI] All supported model type: {}", supportModelType());
  }

  public ChatClientBuilder getBuilderDefault() {
    return getBuilder(properties.getDefaultModelType());
  }

  public ChatClientBuilder getBuilder(ModelType modelType) {
    ChatClientBuilder builder = lookup(modelType);
    if (builder == null) {
      throw new IllegalStateException("不支持的模型类型：" + modelType + ", 仅支持: " + supportModelType());
    }
    return builder;
  }

  private ChatClientBuilder lookup(ModelType modelType) {
    for (ChatClientBuilder builder : builders) {
      if (Objects.equals(builder.getModelType(), modelType)) {
        return builder;
      }
    }

    return null;
  }

  private Set<ModelType> supportModelType() {
    return builders.stream().map(ChatClientBuilder::getModelType).collect(Collectors.toSet());
  }
}
