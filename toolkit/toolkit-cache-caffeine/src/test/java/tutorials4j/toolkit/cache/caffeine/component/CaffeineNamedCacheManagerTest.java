package tutorials4j.toolkit.cache.caffeine.component;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.benmanes.caffeine.cache.Cache;
import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cache.caffeine.CaffeineCache;
import tutorials4j.toolkit.cache.caffeine.autoconfigure.CaffeineCacheToolkitProperties;
import tutorials4j.toolkit.cache.caffeine.autoconfigure.NamedCacheOptions;
import tutorials4j.toolkit.core.exception.ErrorCodeException;

/**
 * {@link CaffeineNamedCacheManager} 单元测试：命名缓存的创建、参数落地与 Spring Cache 适配。
 *
 * @author YunJiao Yang
 */
class CaffeineNamedCacheManagerTest {
  private static final String CACHE_NAME = "users";

  private static CaffeineCacheToolkitProperties properties(NamedCacheOptions options) {
    CaffeineCacheToolkitProperties properties = new CaffeineCacheToolkitProperties();
    properties.getCaches().put(CACHE_NAME, options);
    return properties;
  }

  private static CaffeineNamedCacheManager manager(NamedCacheOptions options) {
    return new CaffeineNamedCacheManager(properties(options));
  }

  private static Cache<Object, Object> nativeCache(NamedCacheOptions options) {
    return manager(options).createNativeCaffeineCache(CACHE_NAME);
  }

  /** 等待条目过期被清除，最多等待 1 秒。 */
  private static void awaitAbsent(Cache<Object, Object> cache, Object key)
      throws InterruptedException {
    for (int i = 0; i < 100 && cache.getIfPresent(key) != null; i++) {
      Thread.sleep(10);
    }
  }

  /** 等待异步淘汰完成，最多等待 1 秒。 */
  private static void awaitSizeAtMost(Cache<Object, Object> cache, long maximum)
      throws InterruptedException {
    for (int i = 0; i < 100 && cache.estimatedSize() > maximum; i++) {
      cache.cleanUp();
      Thread.sleep(10);
    }
  }

  @Test
  @DisplayName("按默认配置创建命名缓存：最大 2000 条、写后 30 秒过期、不记录统计")
  void shouldCreateNativeCacheWithDefaultOptions() {
    Cache<Object, Object> cache = nativeCache(new NamedCacheOptions());

    assertThat(cache.policy().eviction()).isPresent();
    assertThat(cache.policy().eviction().orElseThrow().getMaximum()).isEqualTo(2000L);
    assertThat(cache.policy().expireAfterWrite()).isPresent();
    assertThat(cache.policy().expireAfterWrite().orElseThrow().getExpiresAfter())
        .isEqualTo(Duration.ofSeconds(30));
    assertThat(cache.policy().expireAfterAccess()).isEmpty();
    assertThat(cache.policy().isRecordingStats()).isFalse();
  }

  @Test
  @DisplayName("maximumSize 配置生效，条目数被限制在最大值以内")
  void shouldApplyMaximumSize() throws InterruptedException {
    NamedCacheOptions options = new NamedCacheOptions();
    options.setMaximumSize(10);

    Cache<Object, Object> cache = nativeCache(options);
    assertThat(cache.policy().eviction().orElseThrow().getMaximum()).isEqualTo(10L);

    for (int i = 0; i < 200; i++) {
      cache.put(i, i);
    }
    awaitSizeAtMost(cache, 10L);

    assertThat(cache.estimatedSize()).isLessThanOrEqualTo(10L);
  }

  @Test
  @DisplayName("expireAfterWrite 配置生效，条目到期后不可见")
  void shouldApplyExpireAfterWrite() throws InterruptedException {
    NamedCacheOptions options = new NamedCacheOptions();
    options.setExpireAfterWrite(Duration.ofMillis(50));

    Cache<Object, Object> cache = nativeCache(options);
    assertThat(cache.policy().expireAfterWrite().orElseThrow().getExpiresAfter())
        .isEqualTo(Duration.ofMillis(50));

    cache.put("key", "value");
    assertThat(cache.getIfPresent("key")).isEqualTo("value");

    awaitAbsent(cache, "key");
    assertThat(cache.getIfPresent("key")).isNull();
  }

  @Test
  @DisplayName("expireAfterAccess 配置生效")
  void shouldApplyExpireAfterAccess() {
    NamedCacheOptions options = new NamedCacheOptions();
    options.setExpireAfterAccess(Duration.ofMinutes(3));

    Cache<Object, Object> cache = nativeCache(options);

    assertThat(cache.policy().expireAfterAccess()).isPresent();
    assertThat(cache.policy().expireAfterAccess().orElseThrow().getExpiresAfter())
        .isEqualTo(Duration.ofMinutes(3));
  }

  @Test
  @DisplayName("未配置 expireAfterAccess 时不启用访问过期")
  void shouldNotEnableExpireAfterAccessWhenNotConfigured() {
    NamedCacheOptions options = new NamedCacheOptions();
    options.setExpireAfterAccess(null);

    assertThat(nativeCache(options).policy().expireAfterAccess()).isEmpty();
  }

  @Test
  @DisplayName("initialCapacity 会传递给 Caffeine（非法值直接报错，说明未被忽略）")
  void shouldApplyInitialCapacity() {
    NamedCacheOptions options = new NamedCacheOptions();
    options.setInitialCapacity(32);
    assertThat(nativeCache(options).getIfPresent("not-exists")).isNull();

    NamedCacheOptions invalid = new NamedCacheOptions();
    invalid.setInitialCapacity(-1);
    assertThatThrownBy(() -> nativeCache(invalid)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("recordStats 开启后记录命中与未命中次数")
  void shouldRecordStatsWhenEnabled() {
    NamedCacheOptions options = new NamedCacheOptions();
    options.setRecordStats(true);

    Cache<Object, Object> cache = nativeCache(options);
    assertThat(cache.policy().isRecordingStats()).isTrue();

    cache.put("key", "value");
    cache.getIfPresent("key");
    cache.getIfPresent("missing");

    assertThat(cache.stats().hitCount()).isEqualTo(1L);
    assertThat(cache.stats().missCount()).isEqualTo(1L);
  }

  @Test
  @DisplayName("未开启 recordStats 时不记录统计")
  void shouldNotRecordStatsByDefault() {
    Cache<Object, Object> cache = nativeCache(new NamedCacheOptions());

    cache.put("key", "value");
    cache.getIfPresent("key");

    assertThat(cache.policy().isRecordingStats()).isFalse();
    assertThat(cache.stats().hitCount()).isZero();
  }

  @Test
  @DisplayName("通过 createNativeCaffeineCache 请求未配置的命名缓存时抛出异常")
  void shouldThrowWhenNativeCacheNotConfigured() {
    CaffeineNamedCacheManager manager = manager(new NamedCacheOptions());

    assertThatThrownBy(() -> manager.createNativeCaffeineCache("missing"))
        .isInstanceOf(ErrorCodeException.class)
        .hasMessageContaining("命名缓存未配置");
  }

  @Test
  @DisplayName("通过 getCache 请求未配置的命名缓存时同样抛出异常")
  void shouldThrowWhenGettingUnconfiguredCache() {
    CaffeineNamedCacheManager manager = manager(new NamedCacheOptions());

    assertThatThrownBy(() -> manager.getCache("missing"))
        .isInstanceOf(ErrorCodeException.class)
        .hasMessageContaining("命名缓存未配置");
  }

  @Test
  @DisplayName("命名缓存未声明时不会回退使用全局配置，而是直接报错")
  void shouldNotFallBackToGlobalOptions() {
    CaffeineCacheToolkitProperties properties = new CaffeineCacheToolkitProperties();
    properties.setMaximumSize(10);
    properties.setExpireAfterWrite(Duration.ofMinutes(1));

    CaffeineNamedCacheManager manager = new CaffeineNamedCacheManager(properties);

    assertThatThrownBy(() -> manager.getCache(CACHE_NAME)).isInstanceOf(ErrorCodeException.class);
  }

  @Test
  @DisplayName("Spring Cache 适配：同名缓存复用同一实例，支持存、取、清除")
  void shouldRoundTripThroughSpringCache() {
    CaffeineNamedCacheManager manager = manager(new NamedCacheOptions());

    CaffeineCache cache = (CaffeineCache) manager.getCache(CACHE_NAME);
    assertThat(cache).isNotNull();
    assertThat(cache.getName()).isEqualTo(CACHE_NAME);
    assertThat(manager.getCache(CACHE_NAME)).isSameAs(cache);

    cache.put("key", "value");
    assertThat(cache.get("key", String.class)).isEqualTo("value");

    cache.evict("key");
    assertThat(cache.get("key")).isNull();
  }

  @Test
  @DisplayName("默认允许缓存空值，空值以 NullValue 占位并可被读回")
  void shouldAllowNullValuesByDefault() {
    CaffeineNamedCacheManager manager = manager(new NamedCacheOptions());
    assertThat(manager.isAllowNullValues()).isTrue();

    CaffeineCache cache = (CaffeineCache) manager.getCache(CACHE_NAME);
    cache.put("key", null);

    assertThat(cache.get("key")).isNotNull();
    assertThat(cache.get("key").get()).isNull();
    assertThat(cache.getNativeCache().getIfPresent("key")).isNotNull();
  }

  @Test
  @DisplayName("cacheNullValues=false 时写入空值抛出 IllegalArgumentException")
  void shouldRejectNullValuesWhenDisabled() {
    CaffeineCacheToolkitProperties properties = properties(new NamedCacheOptions());
    properties.setCacheNullValues(false);

    CaffeineNamedCacheManager manager = new CaffeineNamedCacheManager(properties);
    assertThat(manager.isAllowNullValues()).isFalse();

    CaffeineCache cache = (CaffeineCache) manager.getCache(CACHE_NAME);
    assertThatThrownBy(() -> cache.put("key", null)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("构造时传入的缓存名称会被预先创建")
  void shouldPreCreateCachesGivenByConstructor() {
    CaffeineNamedCacheManager manager =
        new CaffeineNamedCacheManager(properties(new NamedCacheOptions()), CACHE_NAME);

    assertThat(manager.getCacheNames()).containsExactly(CACHE_NAME);
    assertThat(manager.getCache(CACHE_NAME)).isNotNull();
  }

  @Test
  @DisplayName("构造时传入未配置的缓存名称会立即抛出异常")
  void shouldFailFastWhenConstructorCacheNameNotConfigured() {
    CaffeineCacheToolkitProperties properties = properties(new NamedCacheOptions());

    assertThatThrownBy(() -> new CaffeineNamedCacheManager(properties, "missing"))
        .isInstanceOf(ErrorCodeException.class)
        .hasMessageContaining("命名缓存未配置");
  }

  @Test
  @DisplayName("构造时传入空的缓存名称数组会关闭动态创建，getCache 返回 null")
  void shouldReturnNullWhenDynamicCreationDisabled() {
    CaffeineNamedCacheManager manager =
        new CaffeineNamedCacheManager(properties(new NamedCacheOptions()), new String[0]);

    assertThat(manager.getCacheNames()).isEmpty();
    assertThat(manager.getCache(CACHE_NAME)).isNull();
  }
}
