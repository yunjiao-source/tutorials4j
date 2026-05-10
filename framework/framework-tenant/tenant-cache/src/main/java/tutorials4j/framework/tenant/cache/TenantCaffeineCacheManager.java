package tutorials4j.framework.tenant.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import tutorials4j.framework.cache.caffeine.CaffeineCacheManagerCreator;
import tutorials4j.framework.common.core.TenantContextHolder;
import tutorials4j.framework.common.core.support.AbstractRoutingCacheManager;

/**
 * 租户隔离的 Caffeine 缓存管理器。
 * <p>继承 {@link AbstractRoutingCacheManager}，根据当前租户 ID 创建或获取独立的
 * {@link CaffeineCacheManager} 实例，实现不同租户的缓存数据完全隔离。</p>
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class TenantCaffeineCacheManager extends AbstractRoutingCacheManager<CaffeineCacheManager> {
    private final CaffeineCacheManagerCreator caffeineCacheManagerCreator;

    /**
     * 确定当前租户的查找键。
     * <p>从 {@link TenantContextHolder} 中获取当前线程绑定的租户标识。</p>
     *
     * @return 租户 ID（可能为 {@code null}，此时外层会抛出异常）
     */
    @NotNull
    @Override
    protected Object determineCurrentLookupKey() {
        return TenantContextHolder.get();
    }

    /**
     * 为指定租户（查找键）创建一个新的 Caffeine 缓存管理器实例。
     * <p>委托给 {@link CaffeineCacheManagerCreator#newInstance()} 生成默认配置的管理器。</p>
     *
     * @param name 租户标识（查找键）
     * @return 新创建的 Caffeine 缓存管理器
     */
    @NotNull
    @Override
    protected CaffeineCacheManager createCacheManager(Object name) {
        if (log.isDebugEnabled()) {
            log.debug("[TENANT-CACHE] 创建[Caffeine]租户缓存管理器: {}", name);
        }

        return caffeineCacheManagerCreator.newInstance();
    }
}
