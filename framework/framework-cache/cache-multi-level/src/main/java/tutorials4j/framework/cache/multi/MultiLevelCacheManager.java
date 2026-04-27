package tutorials4j.framework.cache.multi;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.AbstractCacheManager;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class MultiLevelCacheManager extends AbstractCacheManager {
    private final CacheManager local;
    private final CacheManager remote;


    @Override
    protected Collection<? extends Cache> loadCaches() {
        // 合并两个管理器的缓存名称
        Set<String> names = new HashSet<>();
        names.addAll(local.getCacheNames());
        names.addAll(remote.getCacheNames());
        return names.stream()
                .map(this::getCache)
                .collect(Collectors.toList());
    }

    @Override
    public Cache getCache(String name) {
        Cache caffeineCache = local.getCache(name);
        Cache redisCache = remote.getCache(name);
        if (caffeineCache == null || redisCache == null) {
            return null;
        }
        return new MultiLevelCache(caffeineCache, redisCache);
    }
}
