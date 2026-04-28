package tutorials4j.framework.tenant.cache;

import lombok.RequiredArgsConstructor;
import tutorials4j.framework.cache.multi.MultiLevelCacheManager;
import tutorials4j.framework.cache.redis.RedisCacheManagerCreator;

import java.util.function.Supplier;

/**
 * 租户级多级缓存管理器的创建器（Supplier 模式）。
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class TenantMultiLevelCacheManagerCreator implements Supplier<MultiLevelCacheManager> {
    private final TenantCaffeineCacheManagerCreator tenantCaffeineCacheManagerCreator;
    private final RedisCacheManagerCreator redisCacheManagerCreator;

    private MultiLevelCacheManager instance;

    /**
     * 获取租户级多级缓存管理器单例。
     * <p>先获取租户级 Caffeine 缓存管理器和 Redis 缓存管理器，然后组装成多级管理器。
     * 使用双重检查锁保证线程安全。</p>
     *
     * @return {@link MultiLevelCacheManager} 单例实例
     */
    @Override
    public MultiLevelCacheManager get() {
        if (instance != null) {
            return instance;
        }

        synchronized (this) {
            if (instance != null) {
                return instance;
            }

            instance = new MultiLevelCacheManager(tenantCaffeineCacheManagerCreator.get(),
                    redisCacheManagerCreator.get());
        }

        return instance;
    }
}
