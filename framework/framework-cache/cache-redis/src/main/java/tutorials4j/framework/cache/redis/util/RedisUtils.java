package tutorials4j.framework.cache.redis.util;

import java.util.Objects;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import tutorials4j.framework.cache.core.CacheNamePrefix;
import tutorials4j.framework.cache.core.properties.NamedCacheOptions;
import tutorials4j.framework.cache.core.properties.NamedCacheProperties;

/**
 * 工具
 *
 * @author Yun Jiao
 */
public interface RedisUtils {
  static CacheKeyPrefix convert(CacheNamePrefix cacheNamePrefix) {
    return name -> CacheKeyPrefix.simple().compute(cacheNamePrefix.compute(name));
  }

  /**
   * 根据给定的 {@link NamedCacheOptions} 属性填充 {@link RedisCacheConfiguration} 配置。
   *
   * @param configuration 原始的 {@link RedisCacheConfiguration} 实例，将基于它进行修改
   * @param prop 包含具体缓存配置的 {@link NamedCacheOptions} 对象
   * @param properties
   * @return 填充后的 {@link RedisCacheConfiguration} 实例
   */
  static RedisCacheConfiguration fillConfiguration(
      RedisCacheConfiguration configuration,
      NamedCacheOptions prop,
      NamedCacheProperties properties) {

    // 独立配置覆盖默认配置
    prop.applyDefaults(properties.getDefaults());

    if (prop.getTimeToLive() != null) {
      configuration = configuration.entryTtl(prop.getTimeToLive());
    }

    if (Objects.equals(prop.getEnableStatistics(), Boolean.FALSE)) {
      configuration = configuration.disableCachingNullValues();
    }

    CacheNamePrefix cacheNamePrefix =
        CacheNamePrefix.tenant().suffix(properties.getCacheNamePrefix());
    configuration = configuration.computePrefixWith(convert(cacheNamePrefix));

    return configuration;
  }
}
