package tutorials4j.framework.cache.multi;

import lombok.RequiredArgsConstructor;
import tutorials4j.framework.cache.caffeine.CaffeineCacheManagerCreator;
import tutorials4j.framework.cache.core.support.CacheManagerCreator;
import tutorials4j.framework.cache.core.support.CacheManagerCreatorCategory;
import tutorials4j.framework.cache.redis.RedisCacheManagerCreator;

/**
 * {@link MultiLevelCacheManager} 的创建器，实现双重检查锁定的单例模式。
 *
 * <p>该创建器组合了 Caffeine（一级缓存）和 Redis（二级缓存）的创建器， 在 {@link #getInstance()} 中通过双重检查锁定保证只有一个 {@link
 * MultiLevelCacheManager} 实例被创建。
 *
 * @author Yun Jiao
 * @see MultiLevelCacheManager
 * @see CaffeineCacheManagerCreator
 * @see RedisCacheManagerCreator
 */
@RequiredArgsConstructor
public class MultiLevelCacheManagerCreator implements CacheManagerCreator<MultiLevelCacheManager> {
  private final CaffeineCacheManagerCreator caffeineCacheManagerCreator;
  private final RedisCacheManagerCreator redisCacheManagerCreator;

  private MultiLevelCacheManager instance;

  /**
   * 获取 {@link MultiLevelCacheManager} 单例实例，使用双重检查锁定保证线程安全。
   *
   * @return 多级缓存管理器实例
   */
  @Override
  public MultiLevelCacheManager getInstance() {
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

  /**
   * 创建一个新的多级缓存管理器，组合 Caffeine 与 Redis 缓存管理器。
   *
   * @return 新建的 {@link MultiLevelCacheManager} 实例
   */
  @Override
  public MultiLevelCacheManager newInstance() {
    return new MultiLevelCacheManager(
        caffeineCacheManagerCreator.getInstance(), redisCacheManagerCreator.getInstance());
  }

  /**
   * 返回该创建器生成的 Bean 类型。
   *
   * @return {@link MultiLevelCacheManager} 类型
   */
  @Override
  public Class<MultiLevelCacheManager> getBeanClass() {
    return MultiLevelCacheManager.class;
  }

  /**
   * 返回该创建器的缓存管理器类别。
   *
   * @return {@link CacheManagerCreatorCategory#MULTI_LEVEL}
   */
  @Override
  public CacheManagerCreatorCategory getCategory() {
    return CacheManagerCreatorCategory.MULTI_LEVEL;
  }
}
