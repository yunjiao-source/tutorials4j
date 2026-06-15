package tutorials4j.framework.feature.signin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class SignInService {
  private final Map<String, SignInTemplate> templateMap = new ConcurrentHashMap<>();
  private final SignInResultHandler signInResultHandler;
  private final SignInProperties properties;

  public SignInTemplate template(String source) {
    Assert.hasText(source, "source must not be null or empty");

    return template(SignInConfig.builder().source(source).build());
  }

  public SignInTemplate template(SignInConfig config) {
    Assert.notNull(config, "config must not be null");

    if (StringUtils.isBlank(config.source())) {
      throw new IllegalStateException("SignInConfig.source must not be null or empty");
    }
    SignInConfig.SignInConfigBuilder builder = SignInConfig.builder().source(config.source());
    if (StringUtils.isBlank(config.keyPrefix())) {
      builder.keyPrefix(properties.getRedisKeyPrefix());
    }

    if (config.expireTime() == null) {
      builder.expireTime(properties.getExpireTime());
    }
    return templateMap.computeIfAbsent(
        config.source(), (key) -> new SignInTemplate(signInResultHandler, builder.build()));
  }
}
