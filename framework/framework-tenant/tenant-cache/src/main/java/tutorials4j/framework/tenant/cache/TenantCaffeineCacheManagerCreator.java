package tutorials4j.framework.tenant.cache;

import lombok.RequiredArgsConstructor;
import tutorials4j.framework.cache.caffeine.CaffeineCacheManagerCreator;

import java.util.function.Supplier;

/**
 * 租户级 Caffeine 缓存管理器的创建器（Supplier 模式）。
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class TenantCaffeineCacheManagerCreator implements Supplier<TenantCaffeineCacheManager> {
    private final CaffeineCacheManagerCreator caffeineCacheManagerCreator;

    private TenantCaffeineCacheManager instance;

    /**
     * 获取租户级 Caffeine 缓存管理器单例。
     * <p>使用双重检查锁保证线程安全且高效。</p>
     *
     * @return {@link TenantCaffeineCacheManager} 单例实例
     */
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
