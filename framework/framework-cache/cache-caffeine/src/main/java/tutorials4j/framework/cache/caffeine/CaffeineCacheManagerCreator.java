package tutorials4j.framework.cache.caffeine;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import tutorials4j.framework.cache.core.properties.CacheCaffeineProperties;

import java.util.function.Supplier;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class CaffeineCacheManagerCreator implements Supplier<CaffeineCacheManager> {
    private final CacheCaffeineProperties properties;
    private final Caffeine<Object, Object> caffeine;

    private CaffeineCacheManager instance;

    @Override
    public CaffeineCacheManager get() {
        if (instance != null) {
            return instance;
        }

        synchronized (this) {
            if (instance != null) {
                return instance;
            }

            FlexibleCaffeineCacheManager caffeineCacheManager = new FlexibleCaffeineCacheManager(properties);
            caffeineCacheManager.setCaffeine(caffeine);

            instance = caffeineCacheManager;
        }

        return instance;
    }
}
