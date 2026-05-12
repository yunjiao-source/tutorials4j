package tutorials4j.framework.tenant.cache;

import lombok.RequiredArgsConstructor;
import tutorials4j.framework.cache.core.support.CacheManagerCreator;
import tutorials4j.framework.cache.core.support.CacheManagerCreatorCategory;
import tutorials4j.framework.cache.multi.MultiLevelCacheManager;
import tutorials4j.framework.cache.redis.RedisCacheManagerCreator;

/**
 * 租户级多级缓存管理器的创建器，实现双重检查锁定的单例模式。
 * <p>
 * 该创建器组合了租户级 Caffeine 缓存管理器（一级缓存）和 Redis 缓存管理器（二级缓存），
 * 用于构建支持租户隔离的多级缓存。
 * </p>
 *
 * @author Yun Jiao
 * @see MultiLevelCacheManager
 * @see TenantCaffeineCacheManagerCreator
 * @see RedisCacheManagerCreator
 */
@RequiredArgsConstructor
public class TenantMultiLevelCacheManagerCreator implements CacheManagerCreator<MultiLevelCacheManager> {
    private final TenantCaffeineCacheManagerCreator tenantCaffeineCacheManagerCreator;
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
        return new MultiLevelCacheManager(tenantCaffeineCacheManagerCreator.getInstance(),
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
