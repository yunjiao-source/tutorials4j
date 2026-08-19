package tutorials4j.framework.cache.caffeine;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import tutorials4j.framework.cache.core.properties.NamedCacheProperties;
import tutorials4j.framework.cache.core.support.CacheManagerCreator;
import tutorials4j.framework.cache.core.support.CacheManagerCreatorCategory;

/**
 * Caffeine 缓存管理器的创建器，实现双重检查锁定的单例模式。
 *
 * <p>该创建器负责创建和缓存 {@link CaffeineCacheManager} 实例。 使用 {@link FlexibleCaffeineCacheManager}
 * 作为实际实现，并允许通过外部配置和 Caffeine 规范进行定制。
 *
 * @author Yun Jiao
 * @see NamedCacheProperties
 * @see FlexibleCaffeineCacheManager
 */
@Slf4j
@RequiredArgsConstructor
public class CaffeineCacheManagerCreator implements CacheManagerCreator<CaffeineCacheManager> {
  private final NamedCacheProperties properties;
  private final Caffeine<Object, Object> caffeine;

  private CaffeineCacheManager instance;

  /**
   * 获取 {@link CaffeineCacheManager} 单例实例，使用双重检查锁定保证线程安全。
   *
   * @return Caffeine 缓存管理器实例
   */
  @Override
  public CaffeineCacheManager getInstance() {
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
   * 创建一个新的 Caffeine 缓存管理器，并应用外部配置的 Caffeine 构建器。
   *
   * @return 新建的 {@link CaffeineCacheManager} 实例
   */
  @Override
  public CaffeineCacheManager newInstance() {
    FlexibleCaffeineCacheManager caffeineCacheManager =
        new FlexibleCaffeineCacheManager(properties);
    caffeineCacheManager.setCaffeine(caffeine);
    return caffeineCacheManager;
  }

  /**
   * 返回该创建器生成的 Bean 类型。
   *
   * @return {@link CaffeineCacheManager} 类型
   */
  @Override
  public Class<CaffeineCacheManager> getBeanClass() {
    return CaffeineCacheManager.class;
  }

  /**
   * 返回该创建器的缓存管理器类别。
   *
   * @return {@link CacheManagerCreatorCategory#CAFFEINE}
   */
  @Override
  public CacheManagerCreatorCategory getCategory() {
    return CacheManagerCreatorCategory.CAFFEINE;
  }
}
