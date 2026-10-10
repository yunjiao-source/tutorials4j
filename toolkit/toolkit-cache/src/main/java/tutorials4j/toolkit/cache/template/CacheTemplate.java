package tutorials4j.toolkit.cache.template;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface CacheTemplate<K, V> {
  Class<V> getValueClass();

  void put(K key, V value);

  V putIfAbsent(K key, V value);

  default V createIfAbsent(K key) {
    if (!exists(key)) {
      return create(key);
    }
    return get(key);
  }

  V valueGenerator(K key);

  default V create(K key) {
    V value = this.valueGenerator(key);
    this.put(key, value);
    return value;
  }

  V get(K key);

  V get(K key, Callable<V> valueLoader);

  boolean exists(K key);

  default boolean putIfAbsent(K key) {
    return putIfAbsent(key, valueGenerator(key)) == null;
  }

  void delete(K key);

  CompletableFuture<?> retrieve(K key);

  CompletableFuture<V> retrieve(K key, Supplier<CompletableFuture<V>> valueLoader);
}
