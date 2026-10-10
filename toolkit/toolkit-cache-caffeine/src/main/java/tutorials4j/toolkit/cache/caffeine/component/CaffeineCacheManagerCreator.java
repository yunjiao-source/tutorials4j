package tutorials4j.toolkit.cache.caffeine.component;

import lombok.RequiredArgsConstructor;
import tutorials4j.toolkit.cache.CacheType;
import tutorials4j.toolkit.cache.caffeine.autoconfigure.CaffeineCacheToolkitProperties;
import tutorials4j.toolkit.cache.component.CacheManagerCreator;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class CaffeineCacheManagerCreator implements CacheManagerCreator<CaffeineNamedCacheManager> {
  private final CaffeineCacheToolkitProperties properties;

  private CaffeineNamedCacheManager instance;

  @Override
  public CacheType getCacheType() {
    return CacheType.caffeine;
  }

  @Override
  public CaffeineNamedCacheManager getInstance() {
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
  public CaffeineNamedCacheManager newInstance() {
    return new CaffeineNamedCacheManager(properties);
  }

  @Override
  public Class<CaffeineNamedCacheManager> getBeanClass() {
    return CaffeineNamedCacheManager.class;
  }
}
