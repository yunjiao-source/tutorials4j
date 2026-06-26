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
 * @author Yun Jiao
 */
@Slf4j
public class CacheManagerCreatorFactory {
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

  public CacheManager findCaffeineCacheManager() {
    return findFirstCacheManagerCreator(TENANT_CAFFEINE, CAFFEINE).getInstance();
  }

  public Map<CacheManagerCreatorCategory, CacheManagerCreator<?>> getCreatorMap() {
    return Collections.unmodifiableMap(creators);
  }

  public void setCreatorMap(Map<CacheManagerCreatorCategory, CacheManagerCreator<?>> creators) {
    this.creators.putAll(creators);
  }

  public Cache findFirstCache(String cacheName, CacheManagerCreatorCategory... categories) {
    Assert.hasText(cacheName, "cacheName must not be null or empty");
    return findFirstCacheManagerCreator(categories).getInstance().getCache(cacheName);
  }

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
