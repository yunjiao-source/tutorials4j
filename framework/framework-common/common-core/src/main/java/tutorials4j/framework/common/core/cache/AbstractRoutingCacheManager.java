package tutorials4j.framework.common.core.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public abstract class AbstractRoutingCacheManager<T extends CacheManager> implements CacheManager {
    @Nullable
    private Map<Object, T> targetCacheManagers = new ConcurrentHashMap<>();

    @Override
    public Cache getCache(String name) {
        return this.determineTargetDataSource().getCache(name);
    }

    @Override
    public Collection<String> getCacheNames() {
        return this.determineTargetDataSource().getCacheNames();
    }

    public void addCacheManager(Object name, T cacheManager) {
        Assert.notNull(name, "name must not be null");
        Assert.notNull(cacheManager, "cacheManager must not be null");
        targetCacheManagers.put(name, cacheManager);
    }

    protected CacheManager determineTargetDataSource() {
        Object lookupKey = this.determineCurrentLookupKey();
        if (lookupKey == null) {
            throw new IllegalStateException("Cannot determine target CacheManager for lookup key [" + String.valueOf(lookupKey) + "]");
        }
        return this.targetCacheManagers.computeIfAbsent(lookupKey, this::createCacheManager);
    }

    @Nullable
    protected abstract Object determineCurrentLookupKey();

    @Nullable
    protected abstract T createCacheManager(Object name);
}
