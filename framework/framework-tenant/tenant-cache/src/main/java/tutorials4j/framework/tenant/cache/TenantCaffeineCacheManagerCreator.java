package tutorials4j.framework.tenant.cache;

import lombok.RequiredArgsConstructor;
import tutorials4j.framework.cache.caffeine.CaffeineCacheManagerCreator;
import tutorials4j.framework.cache.core.support.CacheManagerCreator;

/**
 * 租户级 Caffeine 缓存管理器的创建器。
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class TenantCaffeineCacheManagerCreator implements CacheManagerCreator<TenantCaffeineCacheManager> {
    private final CaffeineCacheManagerCreator caffeineCacheManagerCreator;

    private TenantCaffeineCacheManager instance;

    /**
     * 获取租户级 Caffeine 缓存管理器单例。
     * <p>使用双重检查锁保证线程安全且高效。</p>
     *
     * @return {@link TenantCaffeineCacheManager} 单例实例
     */
    @Override
    public TenantCaffeineCacheManager getInstance() {
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
    public TenantCaffeineCacheManager newInstance() {
        return new TenantCaffeineCacheManager(caffeineCacheManagerCreator);
    }

    @Override
    public Class<TenantCaffeineCacheManager> getCacheManagerClass() {
        return TenantCaffeineCacheManager.class;
    }
}
