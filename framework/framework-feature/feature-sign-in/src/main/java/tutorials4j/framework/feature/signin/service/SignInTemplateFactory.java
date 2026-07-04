package tutorials4j.framework.feature.signin.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import tutorials4j.framework.feature.signin.properties.SignInFeatureProperties;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class SignInTemplateFactory {
  private final Map<String, SignInTemplate> templateMap = new ConcurrentHashMap<>();
  private final List<SignInResultHandler> signInResultHandlers;
  private final SignInFeatureProperties properties;

  public SignInTemplate template(String source) {
    Assert.hasText(source, "source must not be null or empty");

    return template(SignInConfig.builder().source(source).build());
  }

  public SignInTemplate template(SignInConfig config) {
    Assert.notNull(config, "config must not be null");

    if (StringUtils.isBlank(config.source())) {
      throw new IllegalStateException("SignInConfig.source must not be null or empty");
    }
    String source = config.source();
    if (!templateMap.containsKey(source)) {
      initTemplate(config);
    }
    return templateMap.get(source);
  }

  private synchronized void initTemplate(SignInConfig config) {
    String source = config.source();
    if (templateMap.containsKey(source)) {
      return;
    }

    SignInConfig.SignInConfigBuilder builder = SignInConfig.builder().source(config.source());
    if (StringUtils.isBlank(config.keyPrefix())) {
      builder.keyPrefix(properties.getRedisKeyPrefix());
    }

    if (config.expireTime() == null) {
      builder.expireTime(properties.getExpireTime());
    }
    builder.maxBits(properties.getMaxBits());

    // 过滤特定source处理器
    List<SignInResultHandler> sourceHandlers =
        signInResultHandlers.stream()
            .filter(e -> e.allSupported() || Objects.equals(e.sourceSupported(), config.source()))
            .toList();

    SignInTemplate template = new SignInTemplate(sourceHandlers, builder.build());
    templateMap.put(source, template);
  }
}
