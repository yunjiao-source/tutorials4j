package tutorials4j.framework.cache.multi;

import lombok.RequiredArgsConstructor;
import tutorials4j.framework.cache.caffeine.CaffeineCacheManagerCreator;
import tutorials4j.framework.cache.core.support.CacheManagerCreator;
import tutorials4j.framework.cache.redis.RedisCacheManagerCreator;

import java.util.function.Supplier;

/**
 * 多级缓存管理器的创建器，实现 {@link CacheManagerCreator} 接口，以单例模式提供 {@link MultiLevelCacheManager} 实例。
 * <p>内部通过双重检查锁（Double-Checked Locking）保证线程安全且只创建一个实例。</p>
 *
 * @author Yun Jiao
 * @see MultiLevelCacheManager
 * @see Supplier
 */
@RequiredArgsConstructor
public class MultiLevelCacheManagerCreator implements CacheManagerCreator<MultiLevelCacheManager> {
    private final CaffeineCacheManagerCreator caffeineCacheManagerCreator;
    private final RedisCacheManagerCreator redisCacheManagerCreator;

    private MultiLevelCacheManager instance;

    /**
     * 获取多级缓存管理器单例。
     * <p>首次调用时会通过本地和远程缓存管理器创建器分别获取底层管理器，并构造 {@link MultiLevelCacheManager} 实例。</p>
     *
     * @return 唯一的多级缓存管理器实例
     */
    @Override
    public MultiLevelCacheManager getInstance() {
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
    public MultiLevelCacheManager newInstance() {
        return new MultiLevelCacheManager(caffeineCacheManagerCreator.getInstance(),
                redisCacheManagerCreator.getInstance());
    }

    @Override
    public Class<MultiLevelCacheManager> getCacheManagerClass() {
        return MultiLevelCacheManager.class;
    }


}
