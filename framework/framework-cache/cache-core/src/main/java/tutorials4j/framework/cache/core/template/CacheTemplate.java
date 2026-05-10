package tutorials4j.framework.cache.core.template;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * 缓存操作模板接口。
 * <p>
 * 定义了缓存的基本操作：存、取、删除、异步加载等。泛型参数 K、V 分别表示缓存键和值的类型。
 * </p>
 *
 * @param <K> 缓存键类型
 * @param <V> 缓存值类型
 * @author Yun Jiao
 */
public interface CacheTemplate<K, V> {
    /**
     * 获取缓存值的类型。
     *
     * @return 值类型的 Class 对象
     */
    Class<V> getValueClass();

    /**
     * 将 key-value 存入缓存。
     *
     * @param key   键
     * @param value 值
     */
    void put(K key, V value);

    /**
     * 如果指定 key 不存在则存入 value，否则返回已有值。
     *
     * @param key   键
     * @param value 值
     * @return 已存在的值或 null（若存入成功则为传入的 value）
     */
    V putIfAbsent(K key, V value);

    /**
     * 根据 key 生成值的逻辑。子类需实现具体生成规则。
     *
     * @param key 键
     * @return 生成的值
     */
    V valueGenerator(K key);

    /**
     * 创建并缓存值：先通过 {@link #valueGenerator(Object)} 生成值，然后存入缓存。
     * <p>
     * 默认实现中会调用 {@link #put(Object, Object)} 存入。
     * </p>
     *
     * @param key 键
     * @return 生成并存入的值
     */
    default V create(K key) {
        V value = this.valueGenerator(key);
        this.put(key, value);
        return value;
    }

    /**
     * 根据 key 获取缓存的值，若不存在则返回 null。
     *
     * @param key 键
     * @return 缓存值，可能为 null
     */
    V get(K key);

    /**
     * 根据 key 获取缓存值，若不存在则通过 valueLoader 加载并存入缓存。
     *
     * @param key         键
     * @param valueLoader 值加载器
     * @return 缓存值（可能由 valueLoader 加载得到）
     */
    V get(K key, Callable<V> valueLoader);

    /**
     * 是否存在键值
     *
     * @param key 键
     * @return 存在值，返回true；否则false
     */
    boolean exists(K key);

    default boolean setIfAbsent(K key) {
        return putIfAbsent(key, valueGenerator(key)) == null;
    }

    /**
     * 删除指定 key 的缓存。
     *
     * @param key 键
     */
    void delete(K key);

    /**
     * 异步获取缓存值（若底层缓存支持 {@link org.springframework.cache.Cache#retrieve(Object)}）。
     *
     * @param key 键
     * @return 包含值的 CompletableFuture，可能为 null
     */
    CompletableFuture<?> retrieve(K key);

    /**
     * 异步获取缓存值，若不存在则通过 valueLoader 异步加载并存入缓存。
     *
     * @param key         键
     * @param valueLoader 异步值加载器，返回 CompletableFuture
     * @return 包含值的 CompletableFuture
     */
    CompletableFuture<V> retrieve(K key, Supplier<CompletableFuture<V>> valueLoader);

}
