package tutorials4j.framework.cache.core.support;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import tutorials4j.framework.cache.core.exception.CacheManagerCreatorNotFoundException;

/**
 * 缓存管理器创建器工厂。
 *
 * @author Yun Jiao
 */
@Slf4j
public class CacheManagerCreatorFactory {
  public static final CacheManagerCreatorFactory instance = new CacheManagerCreatorFactory();
  private EnumMap<CacheManagerCreatorCategory, CacheManagerCreator<?>> creators =
      new EnumMap<>(CacheManagerCreatorCategory.class);

  public CacheManagerCreator<?> getCacheManagerCreator(CacheManagerCreatorCategory category) {
    return creators.get(category);
  }

  /**
   * 根据缓存名称获取多级缓存（两级缓存）实例。
   *
   * @param cacheName 缓存名称
   * @return 多级缓存实例
   * @throws CacheManagerCreatorNotFoundException 如果未找到对应的缓存管理器创建器
   */
  public Cache findMultiLevelCache(String cacheName) {
    CacheManagerCreator<?> creator =
        getCacheManagerCreator(CacheManagerCreatorCategory.TENANT_MULTI_LEVEL);
    if (creator != null) {
      return creator.getInstance().getCache(cacheName);
    }

    creator = getCacheManagerCreator(CacheManagerCreatorCategory.MULTI_LEVEL);
    if (creator != null) {
      return creator.getInstance().getCache(cacheName);
    }

    throw new CacheManagerCreatorNotFoundException("获取两级缓存管理器失败");
  }

  /**
   * 根据缓存名称获取 Redis 缓存实例。
   *
   * @param cacheName 缓存名称
   * @return Redis 缓存实例
   * @throws CacheManagerCreatorNotFoundException 如果未找到对应的缓存管理器创建器
   */
  public Cache findRedisCache(String cacheName) {
    CacheManagerCreator<?> creator = getCacheManagerCreator(CacheManagerCreatorCategory.REDIS);
    if (creator != null) {
      return creator.getInstance().getCache(cacheName);
    }

    throw new CacheManagerCreatorNotFoundException("获取Redis缓存管理器失败");
  }

  /**
   * 根据缓存名称获取 Caffeine 本地缓存实例。
   *
   * <p>优先获取支持租户的 Caffeine 缓存管理器，若不存在则获取普通 Caffeine 缓存管理器。
   *
   * @param cacheName 缓存名称
   * @return Caffeine 缓存实例
   * @throws CacheManagerCreatorNotFoundException 如果未找到任何本地缓存管理器创建器
   */
  public Cache findCaffeineCache(String cacheName) {
    // 先获取支持租户的
    CacheManagerCreator<?> creator =
        getCacheManagerCreator(CacheManagerCreatorCategory.TENANT_CAFFEINE);
    if (creator != null) {
      return creator.getInstance().getCache(cacheName);
    }

    creator = getCacheManagerCreator(CacheManagerCreatorCategory.CAFFEINE);
    if (creator != null) {
      return creator.getInstance().getCache(cacheName);
    }

    throw new CacheManagerCreatorNotFoundException("获取本地缓存管理器失败");
  }

  public Map<CacheManagerCreatorCategory, CacheManagerCreator<?>> getCreatorMap() {
    return Collections.unmodifiableMap(creators);
  }

  public void setCreatorMap(Map<CacheManagerCreatorCategory, CacheManagerCreator<?>> creators) {
    this.creators.putAll(creators);
  }
}
