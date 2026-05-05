package tutorials4j.framework.cache.core.support;

import org.springframework.cache.CacheManager;

/**
 * {@code CacheManager} 创建器，用于生成指定类型的缓存管理器实例。
 * <p>
 * 实现类需提供 {@link CacheManager} 的创建逻辑，并支持单例复用（通过 {@link #getInstance()}）
 * 与每次新建（通过 {@link #newInstance()}）两种方式。
 *
 * @param <T> 具体 {@link CacheManager} 的类型
 * @author Yun Jiao
 */
public interface CacheManagerCreator<T extends CacheManager> {
    /**
     * 获取（或创建）单例的缓存管理器实例。
     * <p>
     * 多次调用应返回同一个实例，实现类需自行保证线程安全。
     *
     * @return 缓存管理器实例
     */
    T getInstance();

    /**
     * 创建一个全新的缓存管理器实例。
     * <p>
     * 每次调用均返回新实例，与已有实例无关。
     *
     * @return 新的缓存管理器实例
     */
    T newInstance();

    /**
     * 返回当前创建器支持的 {@link CacheManager} 实际类型。
     *
     * @return 缓存管理器的 {@link Class} 对象
     */
    Class<T> getCacheManagerClass();
}
