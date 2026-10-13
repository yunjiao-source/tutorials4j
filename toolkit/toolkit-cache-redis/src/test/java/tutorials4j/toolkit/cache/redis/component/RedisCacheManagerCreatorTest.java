package tutorials4j.toolkit.cache.redis.component;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import tutorials4j.toolkit.cache.CacheType;
import tutorials4j.toolkit.cache.redis.autoconfigure.NamedCacheOptions;
import tutorials4j.toolkit.cache.redis.autoconfigure.RedisCacheToolkitProperties;

/**
 * {@link RedisCacheManagerCreator} 单元测试：缓存管理器创建与各类自定义扩展点。
 *
 * @author YunJiao Yang
 */
class RedisCacheManagerCreatorTest {
  private static final String CACHE_NAME = "users";

  private final RedisConnectionFactory connectionFactory = mock(RedisConnectionFactory.class);
  private final GenericJacksonJsonRedisSerializer serializer =
      mock(GenericJacksonJsonRedisSerializer.class);

  private static RedisCacheToolkitProperties properties(NamedCacheOptions options) {
    RedisCacheToolkitProperties properties = new RedisCacheToolkitProperties();
    properties.getCaches().put(CACHE_NAME, options);
    return properties;
  }

  private RedisCacheManagerCreator creator(
      RedisCacheToolkitProperties properties,
      List<RedisNamedCacheConfigurationCustomizer> configurationCustomizers,
      List<RedisNamedCacheManagerCustomizer> managerCustomizers) {
    return new RedisCacheManagerCreator(
        properties, connectionFactory, configurationCustomizers, managerCustomizers, serializer);
  }

  private RedisCacheManagerCreator creator(RedisCacheToolkitProperties properties) {
    return creator(properties, List.of(), List.of());
  }

  @Test
  @DisplayName("缓存类型为 redis，Bean 类型为 RedisNamedCacheManager")
  void shouldExposeTypeAndBeanClass() {
    RedisCacheManagerCreator creator = creator(properties(new NamedCacheOptions()));

    assertThat(creator.getCacheType()).isEqualTo(CacheType.redis);
    assertThat(creator.getBeanClass()).isEqualTo(RedisNamedCacheManager.class);
  }

  @Test
  @DisplayName("getInstance 复用同一实例")
  void shouldReuseInstance() {
    RedisCacheManagerCreator creator = creator(properties(new NamedCacheOptions()));

    RedisNamedCacheManager first = creator.getInstance();

    assertThat(first).isNotNull();
    assertThat(creator.getInstance()).isSameAs(first);
  }

  @Test
  @DisplayName("newInstance 每次返回不同实例")
  void shouldCreateNewInstanceEachTime() {
    RedisCacheManagerCreator creator = creator(properties(new NamedCacheOptions()));

    RedisNamedCacheManager first = creator.newInstance();

    assertThat(first).isNotNull();
    assertThat(creator.newInstance()).isNotSameAs(first);
  }

  @Test
  @DisplayName("创建出的管理器按命名配置提供缓存")
  void shouldCreateWorkingNamedCacheManager() {
    NamedCacheOptions options = new NamedCacheOptions();
    options.setTimeToLive(Duration.ofMinutes(2));
    RedisNamedCacheManager manager = creator(properties(options)).newInstance();

    RedisCache cache = (RedisCache) manager.getCache(CACHE_NAME);

    assertThat(cache).isNotNull();
    assertThat(cache.getCacheConfiguration().getTtlFunction().getTimeToLive(CACHE_NAME, null))
        .isEqualTo(Duration.ofMinutes(2));
  }

  @Test
  @DisplayName("事务感知被关闭")
  void shouldDisableTransactionAware() {
    RedisNamedCacheManager manager = creator(properties(new NamedCacheOptions())).newInstance();

    assertThat(manager.isTransactionAware()).isFalse();
  }

  @Test
  @DisplayName("配置自定义器按顺序生效，最后一个决定最终配置")
  void shouldApplyConfigurationCustomizersInOrder() {
    List<String> calls = new ArrayList<>();
    RedisNamedCacheConfigurationCustomizer first =
        configuration -> {
          calls.add("first");
          return configuration.prefixCacheNameWith("first:");
        };
    RedisNamedCacheConfigurationCustomizer second =
        configuration -> {
          calls.add("second");
          return configuration.prefixCacheNameWith("second:");
        };

    RedisNamedCacheManager manager =
        creator(properties(new NamedCacheOptions()), List.of(first, second), List.of())
            .newInstance();

    RedisCache cache = (RedisCache) manager.getCache(CACHE_NAME);

    assertThat(calls).containsExactly("first", "second");
    assertThat(cache.getCacheConfiguration().getKeyPrefixFor(CACHE_NAME))
        .isEqualTo("second:users::");
  }

  @Test
  @DisplayName("管理器自定义器在初始化前收到管理器实例")
  void shouldApplyManagerCustomizers() {
    RedisNamedCacheManagerCustomizer customizer = mock(RedisNamedCacheManagerCustomizer.class);

    RedisNamedCacheManager manager =
        creator(properties(new NamedCacheOptions()), List.of(), List.of(customizer)).newInstance();

    verify(customizer).customize(manager);
  }

  @Test
  @DisplayName("管理器自定义器可覆盖创建器的初始设置")
  void shouldAllowCustomizerToOverrideInitialSettings() {
    RedisNamedCacheManagerCustomizer customizer =
        cacheManager -> cacheManager.setTransactionAware(true);

    RedisNamedCacheManager manager =
        creator(properties(new NamedCacheOptions()), List.of(), List.of(customizer)).newInstance();

    assertThat(manager.isTransactionAware()).isTrue();
  }

  @Test
  @DisplayName("开启统计时同样可以创建可用的缓存管理器")
  void shouldCreateManagerWithStatisticsEnabled() {
    RedisCacheToolkitProperties properties = properties(new NamedCacheOptions());
    properties.setEnableStatistics(true);

    RedisNamedCacheManager manager = creator(properties).newInstance();

    assertThat(manager.getCache(CACHE_NAME)).isNotNull();
    assertThat(manager.isTransactionAware()).isFalse();
  }
}
