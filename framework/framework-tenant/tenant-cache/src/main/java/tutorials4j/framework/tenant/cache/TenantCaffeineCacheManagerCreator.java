package tutorials4j.framework.tenant.cache;

import lombok.RequiredArgsConstructor;
import tutorials4j.framework.cache.caffeine.CaffeineCacheManagerCreator;

import java.util.function.Supplier;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class TenantCaffeineCacheManagerCreator implements Supplier<TenantCaffeineCacheManager> {
    private final CaffeineCacheManagerCreator caffeineCacheManagerCreator;

    private TenantCaffeineCacheManager instance;

    @Override
    public TenantCaffeineCacheManager get() {
        if (instance != null) {
            return instance;
        }

        synchronized (this) {
            if (instance != null) {
                return instance;
            }



            instance = new TenantCaffeineCacheManager(caffeineCacheManagerCreator);
        }

        return instance;
    }
}
