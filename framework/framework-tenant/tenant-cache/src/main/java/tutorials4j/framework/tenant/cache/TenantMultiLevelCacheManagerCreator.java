package tutorials4j.framework.tenant.cache;

import lombok.RequiredArgsConstructor;
import tutorials4j.framework.cache.core.support.CacheManagerCreator;
import tutorials4j.framework.cache.core.support.CacheManagerCreatorCategory;
import tutorials4j.framework.cache.multi.MultiLevelCacheManager;
import tutorials4j.framework.cache.redis.RedisCacheManagerCreator;

/**
 * 租户级多级缓存管理器的创建器，实现双重检查锁定的单例模式。
 *
 * <p>该创建器组合了租户级 Caffeine 缓存管理器（一级缓存）和 Redis 缓存管理器（二级缓存）， 用于构建支持租户隔离的多级缓存。
 *
 * @author Yun Jiao
 * @see MultiLevelCacheManager
 * @see TenantCaffeineCacheManagerCreator
 * @see RedisCacheManagerCreator
 */
@RequiredArgsConstructor
public class TenantMultiLevelCacheManagerCreator
    implements CacheManagerCreator<MultiLevelCacheManager> {
  private final TenantCaffeineCacheManagerCreator tenantCaffeineCacheManagerCreator;
  private final RedisCacheManagerCreator redisCacheManagerCreator;

  private MultiLevelCacheManager instance;

  /**
   * 获取租户级多级缓存管理器单例。
   *
   * <p>使用双重检查锁保证线程安全且高效。
   *
   * @return {@link MultiLevelCacheManager} 单例实例
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
   * 创建新的租户级多级缓存管理器实例，组合租户级 Caffeine 缓存管理器（一级缓存）与 Redis 缓存管理器（二级缓存）。
   *
   * @return 新的租户级多级缓存管理器实例
   */
  @Override
  public MultiLevelCacheManager newInstance() {
    return new MultiLevelCacheManager(
        tenantCaffeineCacheManagerCreator.getInstance(), redisCacheManagerCreator.getInstance());
  }

  /**
   * 返回缓存管理器的 Bean 类型。
   *
   * @return {@link MultiLevelCacheManager} 类型
   */
  @Override
  public Class<MultiLevelCacheManager> getBeanClass() {
    return MultiLevelCacheManager.class;
  }

  /**
   * 返回缓存管理器创建器的类别。
   *
   * @return {@link CacheManagerCreatorCategory#TENANT_MULTI_LEVEL}
   */
  @Override
  public CacheManagerCreatorCategory getCategory() {
    return CacheManagerCreatorCategory.TENANT_MULTI_LEVEL;
  }
}
