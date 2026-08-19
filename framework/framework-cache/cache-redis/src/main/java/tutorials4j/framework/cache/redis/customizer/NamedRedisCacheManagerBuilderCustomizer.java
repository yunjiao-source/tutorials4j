package tutorials4j.framework.cache.redis.customizer;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import tutorials4j.framework.cache.core.properties.NamedCacheOptions;
import tutorials4j.framework.cache.core.properties.NamedCacheProperties;
import tutorials4j.framework.cache.redis.util.RedisUtils;

/**
 * 命名缓存管理器构建器定制器。
 *
 * <p>该类实现 {@link RedisCacheManagerBuilderCustomizer} 接口，用于在构建 {@link RedisCacheManager} 时， 根据
 * {@link NamedCacheProperties} 中配置的命名缓存（named caches）为每个缓存独立设置 {@link RedisCacheConfiguration} 配置（如
 * TTL、是否允许缓存 null 值、键前缀等）。
 *
 * <p>如果未配置任何命名缓存，则不会生成任何命名缓存的独立配置，仅保留构建器原有的默认配置。
 *
 * @author Yun Jiao
 * @see RedisCacheManagerBuilderCustomizer
 * @see NamedCacheProperties
 */
@Slf4j
@RequiredArgsConstructor
public class NamedRedisCacheManagerBuilderCustomizer implements RedisCacheManagerBuilderCustomizer {

  /** Redis 缓存相关配置属性，包含默认配置和命名缓存配置。 */
  private final NamedCacheProperties properties;

  /**
   * 自定义 {@link RedisCacheManager.RedisCacheManagerBuilder}。
   *
   * <p>遍历 {@link NamedCacheProperties#getCaches()} 中配置的每个命名缓存， 基于构建器上已有的默认配置， 通过 {@link
   * RedisUtils#fillConfiguration} 生成独立的 {@link RedisCacheConfiguration} （包含 TTL、是否允许缓存 null
   * 值、键前缀等），并注册为构建器的初始缓存配置。
   *
   * <p>同时保留构建器原有的默认配置，作为未单独配置的缓存的兜底配置。
   *
   * @param builder 要自定义的 {@link RedisCacheManager.RedisCacheManagerBuilder} 实例
   */
  @Override
  public void customize(RedisCacheManager.RedisCacheManagerBuilder builder) {
    RedisCacheConfiguration defaultConfig = builder.cacheDefaults();
    Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
    Map<String, NamedCacheOptions> redisOption = properties.getCaches();

    // 命名缓存配置
    redisOption.forEach(
        (key, redisProp) -> {
          RedisCacheConfiguration namedCacheConfiguration =
              RedisUtils.fillConfiguration(defaultConfig, redisProp, properties);
          configMap.put(key, namedCacheConfiguration);
        });

    builder.cacheDefaults(defaultConfig).withInitialCacheConfigurations(configMap);
    log.trace(
        "[CACHE-REDIS] RedisCacheManager initialized with cache names: {}", configMap.keySet());
  }
}
