package tutorials4j.framework.cache.redis.util;

import java.util.Objects;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import tutorials4j.framework.cache.core.CacheNamePrefix;
import tutorials4j.framework.cache.core.properties.NamedCacheOptions;
import tutorials4j.framework.cache.core.properties.NamedCacheProperties;

/**
 * Redis 缓存工具接口。
 *
 * <p>提供将 {@link CacheNamePrefix} 转换为 Spring Data Redis 的 {@link CacheKeyPrefix}， 以及根据 {@link
 * NamedCacheOptions} 属性填充 {@link RedisCacheConfiguration} 配置的静态工具方法。
 *
 * @author Yun Jiao
 */
public interface RedisUtils {
  /**
   * 将 {@link CacheNamePrefix} 转换为 {@link CacheKeyPrefix}。
   *
   * @param cacheNamePrefix 自定义缓存名前缀计算器
   * @return 适配后的 {@link CacheKeyPrefix}，其前缀计算逻辑为对原结果再次应用 Redis 默认前缀规则
   */
  static CacheKeyPrefix convert(CacheNamePrefix cacheNamePrefix) {
    return name -> CacheKeyPrefix.simple().compute(cacheNamePrefix.compute(name));
  }

  /**
   * 根据给定的 {@link NamedCacheOptions} 属性填充 {@link RedisCacheConfiguration} 配置。
   *
   * @param configuration 原始的 {@link RedisCacheConfiguration} 实例，将基于它进行修改
   * @param prop 包含具体缓存配置的 {@link NamedCacheOptions} 对象
   * @param properties 全局命名缓存配置属性，用于提供默认配置与缓存名前缀
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
