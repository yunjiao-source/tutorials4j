package tutorials4j.framework.cache.core.properties;

import java.time.Duration;
import lombok.Data;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * 命名缓存的配置选项。
 *
 * <p>包含各类缓存（如 Redis、Caffeine）的通用配置以及特定实现的相关配置。 支持时间到期、空值缓存、统计开关等通用设置，以及 Redis 的键前缀、Caffeine
 * 的初始容量等特有设置。
 *
 * @author Yun Jiao
 * @see NamedCacheProperties
 */
@Data
public class NamedCacheOptions {
  /** 缓存条目的存活时间（TTL）。 若为 {@code null} 则使用默认配置值。 */
  private Duration timeToLive;

  /** 是否允许缓存 {@code null} 值。 若为 {@code null} 则使用默认配置值。 */
  private Boolean cacheNullValues;

  /** 是否启用缓存统计（如命中率、miss 率等）。 若为 {@code null} 则使用默认配置值。 */
  private Boolean enableStatistics;

  /** Redis 缓存的专属配置，例如键前缀。 */
  @NestedConfigurationProperty private RedisOptions redis = new RedisOptions();

  /** Caffeine 缓存的专属配置，例如初始容量、最大大小等。 */
  @NestedConfigurationProperty private CaffeineOptions caffeine = new CaffeineOptions();

  /** Redis 缓存的配置选项。 */
  @Data
  public static class RedisOptions {
    /** Redis 缓存键的前缀，用于区分不同缓存或应用。 若未指定则可能使用默认前缀或无前缀。 */
    private String cachePrefix;
  }

  /** Caffeine 缓存的配置选项。 */
  @Data
  public static class CaffeineOptions {
    /** 初始容量，默认为 100。 */
    private Integer initialCapacity = 100;

    /** 最大缓存条目数，默认为 1000。 */
    private Long maximumSize = 1000L;

    /** 最后一次访问后到期的时间（访问过期）。 若为 {@code null} 则表示不基于访问到期。 */
    private Duration expireAfterAccess;
  }

  /**
   * 将未设置的属性（值为 {@code null}）从默认配置中合并。
   *
   * <p>对于时间到期、空值缓存开关、统计开关以及 Redis 键前缀， 若当前对象中该属性为 {@code null}，则使用默认配置中的对应值。 对于 Caffeine
   * 配置中的初始容量和访问过期时间，直接使用默认配置中的值（覆盖当前值）。
   *
   * @param defaults 默认配置，若为 {@code null} 则方法直接返回，不执行任何操作
   */
  public void applyDefaults(NamedCacheOptions defaults) {
    if (defaults == null) return;

    if (this.timeToLive == null) {
      this.timeToLive = defaults.timeToLive;
    }

    if (this.cacheNullValues == null) {
      this.cacheNullValues = defaults.cacheNullValues;
    }

    if (this.enableStatistics == null) {
      this.enableStatistics = defaults.enableStatistics;
    }

    if (this.redis.cachePrefix == null) {
      this.redis.cachePrefix = defaults.redis.cachePrefix;
    }

    if (this.caffeine.initialCapacity == null) {
      this.caffeine.initialCapacity = defaults.caffeine.initialCapacity;
    }
    if (this.caffeine.maximumSize == null) {
      this.caffeine.maximumSize = defaults.caffeine.maximumSize;
    }
    if (this.caffeine.expireAfterAccess == null) {
      this.caffeine.expireAfterAccess = defaults.caffeine.expireAfterAccess;
    }
  }
}
