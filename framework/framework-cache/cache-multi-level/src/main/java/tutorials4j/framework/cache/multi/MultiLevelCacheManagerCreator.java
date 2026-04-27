package tutorials4j.framework.cache.multi;

import lombok.RequiredArgsConstructor;
import tutorials4j.framework.cache.caffeine.CaffeineCacheManagerCreator;
import tutorials4j.framework.cache.redis.RedisCacheManagerCreator;

import java.util.function.Supplier;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class MultiLevelCacheManagerCreator implements Supplier<MultiLevelCacheManager> {
    private final CaffeineCacheManagerCreator caffeineCacheManagerCreator;
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

            instance = new MultiLevelCacheManager(caffeineCacheManagerCreator.get(),
                    redisCacheManagerCreator.get());
        }

        return instance;
    }
}
