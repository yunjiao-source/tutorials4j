package tutorials4j.framework.data.hibernate.spi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.spi.QueryKey;
import org.hibernate.cache.spi.support.DomainDataStorageAccess;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.springframework.cache.Cache;
import tutorials4j.framework.cache.core.support.CacheManagerCreatorFactory;
import tutorials4j.framework.cache.core.support.CacheType;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class CacheDomainDataStorageAccess implements DomainDataStorageAccess {
  private final String regionName;
  private final CacheType cacheType;

  private Cache cache;

  private Cache getCache() {
    if (cache == null) {
      initCache();
    }
    return cache;
  }

  private synchronized void initCache() {
    if (cache != null) {
      return;
    }

    cache =
        CacheManagerCreatorFactory.instance
            .findFirstCacheManagerCreator(cacheType.getCreatorCategories())
            .getInstance()
            .getCache(regionName);
  }

  private String wrapper(Object key) {
    if (key instanceof QueryKey queryKey) {
      int hashCode = queryKey.hashCode();
      return String.valueOf(hashCode);
    }
    return String.valueOf(key);
  }

  @Override
  public Object getFromCache(Object key, SharedSessionContractImplementor session) {
    Cache.ValueWrapper valueWrapper = getCache().get(wrapper(key));
    return valueWrapper != null ? valueWrapper.get() : null;
  }

  @Override
  public void putIntoCache(Object key, Object value, SharedSessionContractImplementor session) {
    getCache().put(wrapper(key), value);
  }

  @Override
  public boolean contains(Object key) {
    return getCache().get(wrapper(key)) != null;
  }

  @Override
  public void evictData() {
    getCache().clear();
  }

  @Override
  public void evictData(Object key) {
    getCache().evict(key);
  }

  @Override
  public void release() {}
}
