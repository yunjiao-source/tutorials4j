package tutorials4j.framework.cache.core.template;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import lombok.Getter;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.cache.Cache;

/**
 * 缓存模板抽象基类。
 *
 * <p>实现了 {@link CacheTemplate} 接口大部分方法，并作为 {@link SmartInitializingSingleton} 在单例初始化后自动调用 {@link
 * #getCache()} 来获取底层 {@link Cache} 实例。子类需提供具体的缓存初始化逻辑以及值类型。
 *
 * @param <K> 缓存键类型
 * @param <V> 缓存值类型
 * @author Yun Jiao
 */
public abstract class AbstractCacheTemplate<K, V> implements CacheTemplate<K, V> {
  private Cache cache;

  @Getter private final String cacheName;

  /**
   * 根据缓存名称创建底层 {@link Cache} 实例。
   *
   * @param cacheName 缓存名称
   * @return 底层缓存实例
   */
  protected abstract Cache doGetCache(String cacheName);

  /**
   * 构造缓存模板，指定缓存名称。
   *
   * @param cacheName 缓存名称
   */
  protected AbstractCacheTemplate(String cacheName) {
    this.cacheName = cacheName;
  }

  /**
   * 懒加载获取底层 {@link Cache} 实例（双重检查锁，线程安全）。
   *
   * @return 底层缓存实例
   */
  protected Cache getCache() {
    if (cache == null) {
      synchronized (this) {
        if (cache == null) {
          cache = doGetCache(cacheName);
        }
      }
    }
    return cache;
  }

  /** 将键值对存入缓存。 */
  @Override
  public void put(K key, V value) {
    getCache().put(key, value);
  }

  /** 若 key 不存在则存入并返回 null，否则返回已有值。 */
  @Override
  @SuppressWarnings("unchecked")
  public V putIfAbsent(K key, V value) {
    return (V) getCache().putIfAbsent(key, value);
  }

  /** 根据 key 获取缓存值，不存在时返回 null。 */
  @Override
  public V get(K key) {
    return getCache().get(key, getValueClass());
  }

  /** 根据 key 获取缓存值，不存在时通过 valueLoader 加载。 */
  @Override
  public V get(K key, Callable<V> valueLoader) {
    return getCache().get(key, valueLoader);
  }

  /** 判断指定 key 是否存在缓存值。 */
  @Override
  public boolean exists(K key) {
    return get(key) != null;
  }

  /** 删除指定 key 的缓存。 */
  @Override
  public void delete(K key) {
    getCache().evict(key);
  }

  /** 异步获取缓存值。 */
  @Override
  public CompletableFuture<?> retrieve(K key) {
    return getCache().retrieve(key);
  }

  /** 异步获取缓存值，不存在时通过 valueLoader 异步加载。 */
  @Override
  public CompletableFuture<V> retrieve(K key, Supplier<CompletableFuture<V>> valueLoader) {
    return getCache().retrieve(key, valueLoader);
  }
}
