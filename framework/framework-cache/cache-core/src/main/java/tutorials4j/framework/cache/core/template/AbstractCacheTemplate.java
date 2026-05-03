package tutorials4j.framework.cache.core.template;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.cache.Cache;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * 缓存模板抽象基类。
 * <p>
 * 实现了 {@link CacheTemplate} 接口大部分方法，并作为 {@link SmartInitializingSingleton} 在单例初始化后自动调用 {@link #initCache()}
 * 来获取底层 {@link Cache} 实例。子类需提供具体的缓存初始化逻辑以及值类型。
 * </p>
 *
 * @param <K> 缓存键类型
 * @param <V> 缓存值类型
 * @author Yun Jiao
 */
public abstract class AbstractCacheTemplate<K, V> implements CacheTemplate<K, V> , SmartInitializingSingleton {
    protected Cache cache;
    protected final String cacheName;

    protected AbstractCacheTemplate(String cacheName) {
        this.cacheName = cacheName;
    }

    @Override
    public void put(K key, V value) {
        cache.put(key, value);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return (V)cache.putIfAbsent(key, value);
    }

    @Override
    public V get(K key) {
        return cache.get(key, getValueClass());
    }

    @Override
    public V get(K key, Callable<V> valueLoader) {
        return cache.get(key, valueLoader);
    }

    @Override
    public void delete(K key) {
        cache.evict(key);
    }

    @Override
    public CompletableFuture<?> retrieve(K key) {
        return cache.retrieve(key);
    }

    @Override
    public CompletableFuture<V> retrieve(K key, Supplier<CompletableFuture<V>> valueLoader) {
        return cache.retrieve(key, valueLoader);
    }

    @Override
    public void afterSingletonsInstantiated() {
        initCache();
    }

    protected abstract void initCache();
}
