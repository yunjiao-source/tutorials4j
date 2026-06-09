package tutorials4j.framework.cache.redis.util;

import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import tutorials4j.framework.cache.core.RedisKeyPrefix;
import tutorials4j.framework.cache.core.properties.NamedCacheOptions;
import tutorials4j.framework.common.core.TenantContextHolder;

/**
 * 工具
 *
 * @author Yun Jiao
 */
public interface RedisUtils {

  /**
   * 创建默认的缓存键前缀策略。
   *
   * <p>通过 {@link TenantContextHolder#get()} 获取全局默认前缀， 然后拼接缓存名称和分隔符（双冒号）。例如，默认前缀为 "myapp"， 缓存名为
   * "users"，则生成的完整前缀为 "myapp:users::"。
   *
   * @return 默认的 {@link CacheKeyPrefix} 实例
   */
  static CacheKeyPrefix tenantCacheKeyPrefix() {
    return name -> RedisKeyPrefix.tenant().compute(name);
  }

  /**
   * 创建带自定义前缀的缓存键前缀策略。
   *
   * <p>若传入的 {@code prefix} 为空或空白，则回退到 {@link #tenantCacheKeyPrefix()}。
   * 否则，如果前缀末尾没有冒号，会自动补充一个冒号，然后组合全局默认前缀、自定义前缀、缓存名称和分隔符。
   *
   * @param prefix 自定义前缀，可为空
   * @return 组合后的 {@link CacheKeyPrefix} 实例
   */
  static CacheKeyPrefix tenantCacheKeyPrefix(String prefix) {
    if (StringUtils.isBlank(prefix)) {
      return tenantCacheKeyPrefix();
    }

    return name -> RedisKeyPrefix.tenantPrefixed(prefix).compute(name);
  }

  /**
   * 根据给定的 {@link NamedCacheOptions} 属性填充 {@link RedisCacheConfiguration} 配置。
   *
   * @param configuration 原始的 {@link RedisCacheConfiguration} 实例，将基于它进行修改
   * @param prop 包含具体缓存配置的 {@link NamedCacheOptions} 对象
   * @return 填充后的 {@link RedisCacheConfiguration} 实例
   */
  static RedisCacheConfiguration fillConfiguration(
      RedisCacheConfiguration configuration, NamedCacheOptions prop) {

    configuration = configuration.computePrefixWith(RedisUtils.tenantCacheKeyPrefix());
    if (prop.getTimeToLive() != null) {
      configuration = configuration.entryTtl(prop.getTimeToLive());
    }

    if (Objects.equals(prop.getEnableStatistics(), Boolean.FALSE)) {
      configuration = configuration.disableCachingNullValues();
    }

    if (StringUtils.isNotBlank(prop.getRedis().getCachePrefix())) {
      configuration =
          configuration.computePrefixWith(
              RedisUtils.tenantCacheKeyPrefix(prop.getRedis().getCachePrefix()));
    }

    return configuration;
  }
}
