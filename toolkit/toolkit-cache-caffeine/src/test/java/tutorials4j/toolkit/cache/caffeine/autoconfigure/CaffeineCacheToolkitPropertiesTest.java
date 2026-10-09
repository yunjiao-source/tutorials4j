package tutorials4j.toolkit.cache.caffeine.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import tutorials4j.toolkit.core.constant.PropertyConsts;

/**
 * {@link CaffeineCacheToolkitProperties} 配置绑定测试。
 *
 * @author Yun Jiao
 */
class CaffeineCacheToolkitPropertiesTest {
  private static final String PREFIX = PropertyConsts.PROPERTY_TOOLKIT_CACHE_CAFFEINE;

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner().withUserConfiguration(TestConfig.class);

  @Configuration(proxyBeanMethods = false)
  @EnableConfigurationProperties(CaffeineCacheToolkitProperties.class)
  static class TestConfig {}

  @Test
  @DisplayName("默认值：允许缓存空值、无命名缓存配置、继承 NamedCacheOptions 默认值")
  void shouldUseDefaultValues() {
    contextRunner.run(
        context -> {
          CaffeineCacheToolkitProperties properties =
              context.getBean(CaffeineCacheToolkitProperties.class);

          assertThat(properties.isCacheNullValues()).isTrue();
          assertThat(properties.getCaches()).isEmpty();

          assertThat(properties.getExpireAfterWrite()).isEqualTo(Duration.ofSeconds(30));
          assertThat(properties.getMaximumSize()).isEqualTo(2000);
          assertThat(properties.isRecordStats()).isFalse();
          assertThat(properties.getInitialCapacity()).isNull();
          assertThat(properties.getExpireAfterAccess()).isNull();
        });
  }

  @Test
  @DisplayName("可绑定 cache-null-values 与多个命名缓存的独立配置")
  void shouldBindNamedCacheOptions() {
    contextRunner
        .withPropertyValues(
            PREFIX + ".cache-null-values=false",
            PREFIX + ".caches.users.maximum-size=100",
            PREFIX + ".caches.users.expire-after-write=10s",
            PREFIX + ".caches.users.expire-after-access=20s",
            PREFIX + ".caches.users.record-stats=true",
            PREFIX + ".caches.users.initial-capacity=16",
            PREFIX + ".caches.orders.maximum-size=500")
        .run(
            context -> {
              CaffeineCacheToolkitProperties properties =
                  context.getBean(CaffeineCacheToolkitProperties.class);

              assertThat(properties.isCacheNullValues()).isFalse();
              assertThat(properties.getCaches()).containsOnlyKeys("users", "orders");

              NamedCacheOptions users = properties.getCaches().get("users");
              assertThat(users.getMaximumSize()).isEqualTo(100);
              assertThat(users.getExpireAfterWrite()).isEqualTo(Duration.ofSeconds(10));
              assertThat(users.getExpireAfterAccess()).isEqualTo(Duration.ofSeconds(20));
              assertThat(users.isRecordStats()).isTrue();
              assertThat(users.getInitialCapacity()).isEqualTo(16);

              NamedCacheOptions orders = properties.getCaches().get("orders");
              assertThat(orders.getMaximumSize()).isEqualTo(500);
            });
  }

  @Test
  @DisplayName("命名缓存未显式配置的项沿用 NamedCacheOptions 默认值")
  void shouldApplyDefaultsToNamedCache() {
    contextRunner
        .withPropertyValues(PREFIX + ".caches.orders.maximum-size=500")
        .run(
            context -> {
              NamedCacheOptions orders =
                  context.getBean(CaffeineCacheToolkitProperties.class).getCaches().get("orders");

              assertThat(orders.getExpireAfterWrite()).isEqualTo(Duration.ofSeconds(30));
              assertThat(orders.getExpireAfterAccess()).isNull();
              assertThat(orders.isRecordStats()).isFalse();
              assertThat(orders.getInitialCapacity()).isNull();
            });
  }

  @Test
  @DisplayName("绑定失败（超大/非法值）时上下文启动报错")
  void shouldFailFastOnInvalidProperty() {
    contextRunner
        .withPropertyValues(PREFIX + ".caches.users.expire-after-write=abc")
        .run(context -> assertThat(context).hasFailed());
  }
}
