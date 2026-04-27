package tutorials4j.framework.cache.caffeine;

import com.github.benmanes.caffeine.cache.Caffeine;
import tutorials4j.framework.cache.core.properties.CaffeineOptions;

import java.util.Objects;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface CaffeineUtils {

    static void copyOption(Caffeine<Object, Object> caffeine, CaffeineOptions options) {
        caffeine
                .initialCapacity(options.getInitialCapacity())
                .maximumSize(options.getMaximumSize());
        if (options.getExpireAfterAccess() != null) {
            caffeine.expireAfterAccess(options.getExpireAfterAccess());
        }

        if (options.getExpireAfterWrite() != null) {
            caffeine.expireAfterWrite(options.getExpireAfterWrite());
        }

        if (options.getRefreshAfterWrite() != null) {
            caffeine.refreshAfterWrite(options.getRefreshAfterWrite());
        }

        if (Objects.equals(Boolean.TRUE,options.getRecordStats())) {
            caffeine.recordStats();
        }
    }
}
