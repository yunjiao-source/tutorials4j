package tutorials4j.framework.cache.redis;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import tutorials4j.framework.cache.core.properties.NamedCacheProperties;
import tutorials4j.framework.cache.core.support.CacheManagerCreator;
import tutorials4j.framework.cache.core.support.CacheManagerCreatorCategory;
import tutorials4j.framework.cache.redis.util.RedisUtils;

/**
 * Redis 缓存管理器的创建器，实现双重检查锁定的单例模式。
 *
 * <p>该创建器负责创建和缓存 {@link RedisCacheManager} 实例。 支持通过 {@link NamedCacheProperties} 配置默认缓存策略，并允许使用自定义的
 * {@link RedisCacheManagerBuilderCustomizer} 和 {@link CacheManagerCustomizer} 进行扩展。
 *
 * @author Yun Jiao
 * @see NamedCacheProperties
 * @see RedisUtils
 */
@RequiredArgsConstructor
public class RedisCacheManagerCreator implements CacheManagerCreator<RedisCacheManager> {
  private final NamedCacheProperties properties;
  private final RedisConnectionFactory factory;
  private final List<RedisCacheManagerBuilderCustomizer> redisCacheManagerBuilderCustomizer;
  private final List<CacheManagerCustomizer<RedisCacheManager>> cacheManagerCustomizer;

  private RedisCacheManager instance;

  @Override
  public RedisCacheManager getInstance() {
    if (instance != null) {
      return instance;
    }

    synchronized (this) {
      if (instance != null) {
        return instance;
      }

      instance = newInstance();
    }

    return instance;
  }

  @Override
  public RedisCacheManager newInstance() {
    RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig();
    // 使用配置默认值
    defaultCacheConfig = RedisUtils.fillConfiguration(defaultCacheConfig, properties.getDefaults());

    RedisCacheManager.RedisCacheManagerBuilder builder =
        RedisCacheManager.builder(factory).cacheDefaults(defaultCacheConfig);
    if (Objects.equals(properties.getDefaults().getEnableStatistics(), Boolean.TRUE)) {
      builder.enableStatistics();
    }
    redisCacheManagerBuilderCustomizer.forEach(customizer -> customizer.customize(builder));

    RedisCacheManager redisCacheManager = builder.build();
    cacheManagerCustomizer.forEach(customizer -> customizer.customize(redisCacheManager));
    return redisCacheManager;
  }

  @Override
  public Class<RedisCacheManager> getBeanClass() {
    return RedisCacheManager.class;
  }

  @Override
  public CacheManagerCreatorCategory getCategory() {
    return CacheManagerCreatorCategory.REDIS;
  }
}
