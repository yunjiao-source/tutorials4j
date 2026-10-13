package tutorials4j.toolkit.cache.template;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.util.Assert;
import tutorials4j.toolkit.cache.CacheType;
import tutorials4j.toolkit.cache.component.CacheManagerFactory;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@RequiredArgsConstructor
public abstract class AbstractCacheTemplate<K, V> implements CacheTemplate<K, V> {
  private final String cacheName;
  private final CacheType cacheType;

  private volatile Cache cache;

  protected Cache getCache() {
    if (cache == null) {
      synchronized (this) {
        if (cache == null) {
          Assert.hasText(cacheName, "cacheName must not be null or empty");
          Assert.notNull(cacheType, "cacheType must not be null");
          cache = CacheManagerFactory.instance.getCacheManager(cacheType).getCache(cacheName);
        }
      }
    }
    return cache;
  }

  @Override
  public void put(K key, V value) {
    getCache().put(key, value);
  }

  @Override
  @SuppressWarnings("unchecked")
  public V putIfAbsent(K key, V value) {
    return (V) getCache().putIfAbsent(key, value);
  }

  @Override
  public V get(K key) {
    return getCache().get(key, getValueClass());
  }

  @Override
  public V get(K key, Callable<V> valueLoader) {
    return getCache().get(key, valueLoader);
  }

  @Override
  public boolean exists(K key) {
    return get(key) != null;
  }

  @Override
  public void delete(K key) {
    getCache().evict(key);
  }

  @Override
  public CompletableFuture<?> retrieve(K key) {
    return getCache().retrieve(key);
  }

  @Override
  public CompletableFuture<V> retrieve(K key, Supplier<CompletableFuture<V>> valueLoader) {
    return getCache().retrieve(key, valueLoader);
  }
}
