package tutorials4j.framework.tenant.cache;

import lombok.RequiredArgsConstructor;
import tutorials4j.framework.cache.multi.MultiLevelCacheManager;
import tutorials4j.framework.cache.redis.RedisCacheManagerCreator;

import java.util.function.Supplier;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class TenantMultiLevelCacheManagerCreator implements Supplier<MultiLevelCacheManager> {
    private final TenantCaffeineCacheManagerCreator tenantCaffeineCacheManagerCreator;
    private final RedisCacheManagerCreator redisCacheManagerCreator;

    private MultiLevelCacheManager instance;

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
