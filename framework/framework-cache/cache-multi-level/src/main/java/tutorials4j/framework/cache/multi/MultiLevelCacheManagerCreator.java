package tutorials4j.framework.cache.multi;

import lombok.RequiredArgsConstructor;
import tutorials4j.framework.cache.caffeine.CaffeineCacheManagerCreator;
import tutorials4j.framework.cache.core.support.CacheManagerCreator;
import tutorials4j.framework.cache.core.support.CacheManagerCreatorCategory;
import tutorials4j.framework.cache.redis.RedisCacheManagerCreator;


/**
 * {@link MultiLevelCacheManager} 的创建器，实现双重检查锁定的单例模式。
 * <p>
 * 该创建器组合了 Caffeine（一级缓存）和 Redis（二级缓存）的创建器，
 * 在 {@link #getInstance()} 中通过双重检查锁定保证只有一个 {@link MultiLevelCacheManager} 实例被创建。
 * </p>
 *
 * @author Yun Jiao
 * @see MultiLevelCacheManager
 * @see CaffeineCacheManagerCreator
 * @see RedisCacheManagerCreator
 */
@RequiredArgsConstructor
public class MultiLevelCacheManagerCreator implements CacheManagerCreator<MultiLevelCacheManager> {
    private final CaffeineCacheManagerCreator caffeineCacheManagerCreator;
    private final RedisCacheManagerCreator redisCacheManagerCreator;

    private MultiLevelCacheManager instance;

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
    public Class<MultiLevelCacheManager> getBeanClass() {
        return MultiLevelCacheManager.class;
    }


    @Override
    public CacheManagerCreatorCategory getCategory() {
        return CacheManagerCreatorCategory.TENANT_MULTI_LEVEL;
    }
}
