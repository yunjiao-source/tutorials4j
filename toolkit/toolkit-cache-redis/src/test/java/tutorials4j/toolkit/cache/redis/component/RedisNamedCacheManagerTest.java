package tutorials4j.toolkit.cache.redis.component;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import tutorials4j.toolkit.cache.redis.autoconfigure.NamedCacheOptions;
import tutorials4j.toolkit.cache.redis.autoconfigure.RedisCacheToolkitProperties;
import tutorials4j.toolkit.core.exception.ErrorCodeException;

/**
 * {@link RedisNamedCacheManager} 单元测试：命名缓存的独立配置与占位。
 *
 * @author YunJiao Yang
 */
class RedisNamedCacheManagerTest {
  private static final String CACHE_NAME = "users";

  private final RedisCacheWriter cacheWriter = mock(RedisCacheWriter.class);

  private static RedisCacheToolkitProperties properties(NamedCacheOptions options) {
    RedisCacheToolkitProperties properties = new RedisCacheToolkitProperties();
    properties.getCaches().put(CACHE_NAME, options);
    return properties;
  }

  private RedisNamedCacheManager manager(RedisCacheToolkitProperties properties) {
    return new RedisNamedCacheManager(
        properties, cacheWriter, RedisCacheConfiguration.defaultCacheConfig());
  }

  private static Duration ttlOf(RedisCache cache) {
    return cache.getCacheConfiguration().getTtlFunction().getTimeToLive(CACHE_NAME, null);
  }

  @Test
  @DisplayName("缓存未配置时抛出常异常")
  void shouldThrowWhenCacheNotConfigured() {
    RedisNamedCacheManager manager = manager(properties(new NamedCacheOptions()));

    assertThatThrownBy(() -> manager.getCache("missing"))
        .isInstanceOf(ErrorCodeException.class)
        .hasMessageContaining("命名缓存未配置");
  }

  @Test
  @DisplayName("caches 为 null 时同样抛出异常")
  void shouldThrowWhenCachesIsNull() {
    RedisCacheToolkitProperties properties = properties(new NamedCacheOptions());
    properties.setCaches(null);
    RedisNamedCacheManager manager = manager(properties);

    assertThatThrownBy(() -> manager.getCache(CACHE_NAME)).isInstanceOf(ErrorCodeException.class);
  }

  @Test
  @DisplayName("按命名缓存配置创建缓存并应用过期时间")
  void shouldApplyPerCacheTtl() {
    NamedCacheOptions options = new NamedCacheOptions();
    options.setTimeToLive(Duration.ofMinutes(5));
    RedisNamedCacheManager manager = manager(properties(options));

    RedisCache cache = (RedisCache) manager.getCache(CACHE_NAME);

    assertThat(cache).isNotNull();
    assertThat(cache.getName()).isEqualTo(CACHE_NAME);
    assertThat(ttlOf(cache)).isEqualTo(Duration.ofMinutes(5));
  }

  @Test
  @DisplayName("命名缓存未配置过期时间时使用默认 30 秒")
  void shouldApplyDefaultTtl() {
    RedisNamedCacheManager manager = manager(properties(new NamedCacheOptions()));

    assertThat(ttlOf((RedisCache) manager.getCache(CACHE_NAME))).isEqualTo(Duration.ofSeconds(30));
  }

  @Test
  @DisplayName("cachingNullValues=false 时禁用空值缓存")
  void shouldDisableCachingNullValuesWhenConfigured() {
    NamedCacheOptions options = new NamedCacheOptions();
    options.setCachingNullValues(false);
    RedisNamedCacheManager manager = manager(properties(options));

    RedisCache cache = (RedisCache) manager.getCache(CACHE_NAME);

    assertThat(cache.getCacheConfiguration().getAllowCacheNullValues()).isFalse();
  }

  @Test
  @DisplayName("默认允许缓存空值")
  void shouldAllowCachingNullValuesByDefault() {
    RedisNamedCacheManager manager = manager(properties(new NamedCacheOptions()));

    RedisCache cache = (RedisCache) manager.getCache(CACHE_NAME);

    assertThat(cache.getCacheConfiguration().getAllowCacheNullValues()).isTrue();
  }

  @Test
  @DisplayName("配置 keyPrefix 时拼接到缓存名前（RedisCacheConfiguration 会追加 :: 分隔符）")
  void shouldApplyKeyPrefix() {
    NamedCacheOptions options = new NamedCacheOptions();
    options.setKeyPrefix("user:");
    RedisNamedCacheManager manager = manager(properties(options));

    RedisCache cache = (RedisCache) manager.getCache(CACHE_NAME);

    assertThat(cache.getCacheConfiguration().getKeyPrefixFor(CACHE_NAME)).isEqualTo("user:users::");
  }

  @Test
  @DisplayName("keyPrefix 为空白时不覆盖默认前缀")
  void shouldIgnoreBlankKeyPrefix() {
    NamedCacheOptions options = new NamedCacheOptions();
    options.setKeyPrefix("   ");
    RedisNamedCacheManager manager = manager(properties(options));

    RedisCache cache = (RedisCache) manager.getCache(CACHE_NAME);

    assertThat(cache.getCacheConfiguration().getKeyPrefixFor(CACHE_NAME)).isEqualTo("users::");
  }

  @Test
  @DisplayName("同名缓存复用同一实例")
  void shouldReuseCacheInstance() {
    RedisNamedCacheManager manager = manager(properties(new NamedCacheOptions()));

    assertThat(manager.getCache(CACHE_NAME)).isSameAs(manager.getCache(CACHE_NAME));
  }
}
