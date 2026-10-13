package tutorials4j.toolkit.cache.caffeine.autoconfigure;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cache.caffeine.CaffeineCache;
import tutorials4j.toolkit.cache.CacheType;
import tutorials4j.toolkit.cache.caffeine.component.CaffeineCacheManagerCreator;
import tutorials4j.toolkit.cache.caffeine.component.CaffeineNamedCacheManager;
import tutorials4j.toolkit.core.constant.PropertyConsts;
import tutorials4j.toolkit.core.exception.ErrorCodeException;

/**
 * {@link CaffeineCacheToolkitAutoConfiguration} 自动装配测试。
 *
 * @author YunJiao Yang
 */
class CaffeineCacheToolkitAutoConfigurationTest {
  private static final String PREFIX = PropertyConsts.PROPERTY_TOOLKIT_CACHE_CAFFEINE;
  private static final String AUTO_CONFIGURATION_IMPORTS =
      "META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports";

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(CaffeineCacheToolkitAutoConfiguration.class));

  @Test
  @DisplayName("装配自动配置类、配置属性与缓存管理器创建器")
  void shouldRegisterBeans() {
    contextRunner.run(
        context -> {
          assertThat(context).hasSingleBean(CaffeineCacheToolkitAutoConfiguration.class);
          assertThat(context).hasSingleBean(CaffeineCacheToolkitProperties.class);
          assertThat(context).hasSingleBean(CaffeineCacheManagerCreator.class);
          assertThat(context).hasBean("caffeineCacheManagerCreator");
        });
  }

  @Test
  @DisplayName("可从环境变量绑定 caffeine 缓存配置")
  void shouldBindPropertiesFromEnvironment() {
    contextRunner
        .withPropertyValues(
            PREFIX + ".cache-null-values=false",
            PREFIX + ".caches.users.maximum-size=100",
            PREFIX + ".caches.users.expire-after-write=10s",
            PREFIX + ".caches.users.record-stats=true")
        .run(
            context -> {
              CaffeineCacheToolkitProperties properties =
                  context.getBean(CaffeineCacheToolkitProperties.class);

              assertThat(properties.isCacheNullValues()).isFalse();

              NamedCacheOptions users = properties.getCaches().get("users");
              assertThat(users).isNotNull();
              assertThat(users.getMaximumSize()).isEqualTo(100);
              assertThat(users.getExpireAfterWrite()).isEqualTo(Duration.ofSeconds(10));
              assertThat(users.isRecordStats()).isTrue();
            });
  }

  @Test
  @DisplayName("通过创建器可得到可用的缓存管理器，并按命名配置创建缓存")
  void shouldProvideUsableCacheManager() {
    contextRunner
        .withPropertyValues(
            PREFIX + ".caches.users.maximum-size=10",
            PREFIX + ".caches.users.expire-after-write=30s")
        .run(
            context -> {
              CaffeineCacheManagerCreator creator =
                  context.getBean(CaffeineCacheManagerCreator.class);

              assertThat(creator.getCacheType()).isEqualTo(CacheType.caffeine);
              assertThat(creator.getBeanClass()).isEqualTo(CaffeineNamedCacheManager.class);

              CaffeineNamedCacheManager cacheManager = creator.getInstance();
              assertThat(cacheManager).isSameAs(creator.getInstance());

              CaffeineCache cache = (CaffeineCache) cacheManager.getCache("users");
              assertThat(cache).isNotNull();
              assertThat(cache.getNativeCache().policy().eviction().orElseThrow().getMaximum())
                  .isEqualTo(10L);

              cache.put("key", "value");
              assertThat(cache.get("key", String.class)).isEqualTo("value");
            });
  }

  @Test
  @DisplayName("未配置命名缓存时，获取缓存会抛出异常")
  void shouldFailWhenNamedCacheNotConfigured() {
    contextRunner.run(
        context -> {
          CaffeineNamedCacheManager cacheManager =
              context.getBean(CaffeineCacheManagerCreator.class).getInstance();

          assertThat(cacheManager.getCacheNames()).isEmpty();
          assertThat(catchThrowable(() -> cacheManager.getCache("users")))
              .isInstanceOf(ErrorCodeException.class);
        });
  }

  @Test
  @DisplayName("自动配置类已登记在 META-INF 自动配置清单中")
  void shouldBeRegisteredInAutoConfigurationImports() throws IOException {
    try (InputStream stream =
        getClass().getClassLoader().getResourceAsStream(AUTO_CONFIGURATION_IMPORTS)) {
      assertThat(stream).isNotNull();
      assertThat(new String(stream.readAllBytes(), UTF_8))
          .contains(CaffeineCacheToolkitAutoConfiguration.class.getName());
    }
  }
}
