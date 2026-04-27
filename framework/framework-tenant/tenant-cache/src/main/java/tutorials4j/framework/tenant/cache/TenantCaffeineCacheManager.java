package tutorials4j.framework.tenant.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import tutorials4j.framework.cache.caffeine.CaffeineCacheManagerCreator;
import tutorials4j.framework.common.core.TenantContextHolder;
import tutorials4j.framework.common.core.cache.AbstractRoutingCacheManager;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class TenantCaffeineCacheManager extends AbstractRoutingCacheManager<CaffeineCacheManager> {
    private final CaffeineCacheManagerCreator caffeineCacheManagerCreator;

    @Override
    protected Object determineCurrentLookupKey() {
        return TenantContextHolder.get();
    }

    @Override
    protected CaffeineCacheManager createCacheManager(Object name) {
        log.debug("Tutorials4j - Cache |- 创建[Caffeine]租户缓存管理器: {}", name);

        return caffeineCacheManagerCreator.get();
    }
}
