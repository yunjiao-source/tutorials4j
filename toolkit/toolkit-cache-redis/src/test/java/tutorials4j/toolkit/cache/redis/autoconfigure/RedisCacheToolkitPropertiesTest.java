package tutorials4j.toolkit.cache.redis.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import tutorials4j.toolkit.core.constant.PropertyConsts;

/**
 * {@link RedisCacheToolkitProperties} 配置绑定测试。
 *
 * @author Yun Jiao
 */
class RedisCacheToolkitPropertiesTest {
  private static final String PREFIX = PropertyConsts.PROPERTY_TOOLKIT_CACHE_REDIS;

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner().withUserConfiguration(TestConfig.class);

  @Configuration(proxyBeanMethods = false)
  @EnableConfigurationProperties(RedisCacheToolkitProperties.class)
  static class TestConfig {}

  @Test
  @DisplayName("默认值：30 秒过期、允许缓存空值、模板前缀 template:、不统计")
  void shouldUseDefaultValues() {
    contextRunner.run(
        context -> {
          RedisCacheToolkitProperties properties =
              context.getBean(RedisCacheToolkitProperties.class);

          assertThat(properties.getTimeToLive()).isEqualTo(Duration.ofSeconds(30));
          assertThat(properties.isCachingNullValues()).isTrue();
          assertThat(properties.getKeyPrefix()).isNull();
          assertThat(properties.getTemplateKeyPrefix()).isEqualTo("template:");
          assertThat(properties.isEnableStatistics()).isFalse();
          assertThat(properties.getSeparator()).isEqualTo("-");
          assertThat(properties.getCaches()).isEmpty();
        });
  }

  @Test
  @DisplayName("可绑定全局配置与命名缓存独立配置")
  void shouldBindNamedCacheOptions() {
    contextRunner
        .withPropertyValues(
            PREFIX + ".time-to-live=5m",
            PREFIX + ".caching-null-values=false",
            PREFIX + ".template-key-prefix=cache:",
            PREFIX + ".enable-statistics=true",
            PREFIX + ".separator=:",
            PREFIX + ".caches.users.time-to-live=10s",
            PREFIX + ".caches.users.caching-null-values=true",
            PREFIX + ".caches.users.key-prefix=user:")
        .run(
            context -> {
              RedisCacheToolkitProperties properties =
                  context.getBean(RedisCacheToolkitProperties.class);

              assertThat(properties.getTimeToLive()).isEqualTo(Duration.ofMinutes(5));
              assertThat(properties.isCachingNullValues()).isFalse();
              assertThat(properties.getTemplateKeyPrefix()).isEqualTo("cache:");
              assertThat(properties.isEnableStatistics()).isTrue();
              assertThat(properties.getSeparator()).isEqualTo(":");

              assertThat(properties.getCaches()).containsOnlyKeys("users");
              NamedCacheOptions users = properties.getCaches().get("users");
              assertThat(users.getTimeToLive()).isEqualTo(Duration.ofSeconds(10));
              assertThat(users.isCachingNullValues()).isTrue();
              assertThat(users.getKeyPrefix()).isEqualTo("user:");
            });
  }

  @Test
  @DisplayName("命名缓存未配置项沿用默认值")
  void shouldApplyDefaultsToNamedCache() {
    contextRunner
        .withPropertyValues(PREFIX + ".caches.orders.key-prefix=order:")
        .run(
            context -> {
              NamedCacheOptions orders =
                  context.getBean(RedisCacheToolkitProperties.class).getCaches().get("orders");

              assertThat(orders.getTimeToLive()).isEqualTo(Duration.ofSeconds(30));
              assertThat(orders.isCachingNullValues()).isTrue();
            });
  }

  @Test
  @DisplayName("绑定非法时长时上下文启动失败")
  void shouldFailOnInvalidDuration() {
    contextRunner
        .withPropertyValues(PREFIX + ".caches.users.time-to-live=abc")
        .run(context -> assertThat(context).hasFailed());
  }
}
