package tutorials4j.framework.cache.core.support;

import static tutorials4j.framework.cache.core.support.CacheManagerCreatorCategory.CAFFEINE;
import static tutorials4j.framework.cache.core.support.CacheManagerCreatorCategory.MULTI_LEVEL;
import static tutorials4j.framework.cache.core.support.CacheManagerCreatorCategory.REDIS;
import static tutorials4j.framework.cache.core.support.CacheManagerCreatorCategory.TENANT_CAFFEINE;
import static tutorials4j.framework.cache.core.support.CacheManagerCreatorCategory.TENANT_MULTI_LEVEL;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.util.Assert;
import tutorials4j.framework.cache.core.exception.CacheErrorCode;

/**
 * 缓存管理器创建器工厂。
 *
 * <p>持有按 {@link CacheManagerCreatorCategory} 分类注册的 {@link CacheManagerCreator} 集合，
 * 提供按缓存类型（Caffeine、Redis、多级缓存等）查找缓存管理器或缓存实例的方法。
 *
 * @author Yun Jiao
 */
@Slf4j
public class CacheManagerCreatorFactory {
  /** 工厂单例实例 */
  public static final CacheManagerCreatorFactory instance = new CacheManagerCreatorFactory();

  private final EnumMap<CacheManagerCreatorCategory, CacheManagerCreator<?>> creators =
      new EnumMap<>(CacheManagerCreatorCategory.class);

  /**
   * 根据缓存名称获取多级缓存（两级缓存）实例。
   *
   * @param cacheName 缓存名称
   * @return 多级缓存实例
   */
  public Cache findMultiLevelCache(String cacheName) {
    return findMultiLevelCacheManager().getCache(cacheName);
  }

  /**
   * 获取多级缓存管理器，优先返回租户多级缓存管理器，其次返回普通多级缓存管理器。
   *
   * @return 多级缓存管理器
   */
  public CacheManager findMultiLevelCacheManager() {
    return findFirstCacheManagerCreator(TENANT_MULTI_LEVEL, MULTI_LEVEL).getInstance();
  }

  /**
   * 根据缓存名称获取 Redis 缓存实例。
   *
   * @param cacheName 缓存名称
   * @return Redis 缓存实例
   */
  public Cache findRedisCache(String cacheName) {
    return findRedisCacheManager().getCache(cacheName);
  }

  /**
   * 获取 Redis 缓存管理器。
   *
   * @return Redis 缓存管理器
   */
  public CacheManager findRedisCacheManager() {
    return findFirstCacheManagerCreator(REDIS).getInstance();
  }

  /**
   * 根据缓存名称获取 Caffeine 本地缓存实例。
   *
   * <p>优先获取支持租户的 Caffeine 缓存管理器，若不存在则获取普通 Caffeine 缓存管理器。
   *
   * @param cacheName 缓存名称
   * @return Caffeine 缓存实例
   */
  public Cache findCaffeineCache(String cacheName) {
    return findCaffeineCacheManager().getCache(cacheName);
  }

  /**
   * 获取 Caffeine 本地缓存管理器，优先返回租户 Caffeine 缓存管理器，其次返回普通 Caffeine 缓存管理器。
   *
   * @return Caffeine 缓存管理器
   */
  public CacheManager findCaffeineCacheManager() {
    return findFirstCacheManagerCreator(TENANT_CAFFEINE, CAFFEINE).getInstance();
  }

  /**
   * 获取所有缓存管理器创建器的不可变映射视图。
   *
   * @return 创建器分类到创建器的映射
   */
  public Map<CacheManagerCreatorCategory, CacheManagerCreator<?>> getCreatorMap() {
    return Collections.unmodifiableMap(creators);
  }

  /**
   * 注册缓存管理器创建器映射。
   *
   * @param creators 创建器分类到创建器的映射
   */
  public void setCreatorMap(Map<CacheManagerCreatorCategory, CacheManagerCreator<?>> creators) {
    this.creators.putAll(creators);
  }

  /**
   * 按给定分类顺序查找第一个可用的缓存实例。
   *
   * @param cacheName 缓存名称
   * @param categories 创建器分类，按优先级从高到低排列
   * @return 找到的缓存实例
   */
  public Cache findFirstCache(String cacheName, CacheManagerCreatorCategory... categories) {
    Assert.hasText(cacheName, "cacheName must not be null or empty");
    return findFirstCacheManagerCreator(categories).getInstance().getCache(cacheName);
  }

  /**
   * 按给定分类顺序查找第一个可用的缓存管理器创建器。
   *
   * @param categories 创建器分类，按优先级从高到低排列
   * @return 找到的缓存管理器创建器
   * @throws tutorials4j.framework.common.core.exception.ErrorCodeException 当所有分类均未注册创建器时抛出
   */
  public CacheManagerCreator<?> findFirstCacheManagerCreator(
      CacheManagerCreatorCategory... categories) {
    return Stream.of(categories)
        .map(creators::get)
        .filter(Objects::nonNull)
        .findFirst()
        .orElseThrow(
            () ->
                CacheErrorCode.CACHE_MANAGER_CREATOR_NOT_EXIST
                    .throwed("获取CacheManagerCreator实例失败")
                    .param("categories", Arrays.toString(categories)));
  }
}
