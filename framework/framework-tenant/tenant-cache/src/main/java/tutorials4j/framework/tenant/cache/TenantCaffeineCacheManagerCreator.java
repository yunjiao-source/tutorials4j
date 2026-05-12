package tutorials4j.framework.tenant.cache;

import lombok.RequiredArgsConstructor;
import tutorials4j.framework.cache.caffeine.CaffeineCacheManagerCreator;
import tutorials4j.framework.cache.core.support.CacheManagerCreator;
import tutorials4j.framework.cache.core.support.CacheManagerCreatorCategory;

/**
 * 租户级 Caffeine 缓存管理器的创建器，实现双重检查锁定的单例模式。
 * <p>
 * 该创建器负责创建和缓存 {@link TenantCaffeineCacheManager} 实例。
 * 它内部委托给普通的 {@link CaffeineCacheManagerCreator} 来完成底层 Caffeine 缓存管理器的构建，
 * 然后用租户隔离的包装器进行装饰。
 * </p>
 *
 * @author Yun Jiao
 * @see TenantCaffeineCacheManager
 * @see CaffeineCacheManagerCreator
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
    public Class<TenantCaffeineCacheManager> getBeanClass() {
        return TenantCaffeineCacheManager.class;
    }

    @Override
    public CacheManagerCreatorCategory getCategory() {
        return CacheManagerCreatorCategory.TENANT_CAFFEINE;
    }
}
