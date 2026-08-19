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
 * 签到模板工厂
 *
 * <p>根据来源（source）创建并缓存对应的 {@link SignInTemplate} 实例，按需补齐默认配置 （Redis 键前缀、过期时间、最大位数），并过滤出匹配该来源的
 * {@link SignInResultHandler}。
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class SignInTemplateFactory {
  private final Map<String, SignInTemplate> templateMap = new ConcurrentHashMap<>();
  private final List<SignInResultHandler> signInResultHandlers;
  private final SignInFeatureProperties properties;

  /**
   * 根据来源标识获取对应的签到模板，其余配置使用默认值
   *
   * @param source 签到来源标识
   * @return 签到模板
   * @throws IllegalArgumentException 当 source 为空时
   */
  public SignInTemplate template(String source) {
    Assert.hasText(source, "source must not be null or empty");

    return template(SignInConfig.builder().source(source).build());
  }

  /**
   * 根据签到配置获取对应的签到模板，缺失的配置项使用默认值补齐
   *
   * @param config 签到配置
   * @return 签到模板
   * @throws IllegalArgumentException 当 config 为 null 时
   * @throws IllegalStateException 当 config.source 为空时
   */
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

  /** 初始化并缓存指定来源的签到模板：补齐默认配置并过滤匹配该来源的处理器 */
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
