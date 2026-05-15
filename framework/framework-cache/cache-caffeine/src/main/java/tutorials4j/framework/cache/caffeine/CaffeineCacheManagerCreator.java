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

  @Override
  public CaffeineCacheManager newInstance() {
    FlexibleCaffeineCacheManager caffeineCacheManager =
        new FlexibleCaffeineCacheManager(properties);
    caffeineCacheManager.setCaffeine(caffeine);
    return caffeineCacheManager;
  }

  @Override
  public Class<CaffeineCacheManager> getBeanClass() {
    return CaffeineCacheManager.class;
  }

  @Override
  public CacheManagerCreatorCategory getCategory() {
    return CacheManagerCreatorCategory.CAFFEINE;
  }
}
