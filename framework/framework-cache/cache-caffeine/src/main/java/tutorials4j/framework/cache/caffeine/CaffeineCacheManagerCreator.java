package tutorials4j.framework.cache.caffeine;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import tutorials4j.framework.cache.core.properties.CacheCaffeineProperties;
import tutorials4j.framework.cache.core.support.CacheManagerCreator;

/**
 * {@link CaffeineCacheManager}的创建器，实现{@link CacheManagerCreator}接口，并提供单例的缓存管理器实例。
 * <p>采用双重检查锁（DCL）保证线程安全且延迟加载。实际创建的缓存管理器为{@link FlexibleCaffeineCacheManager}，
 * 支持每个缓存名称的独立配置。</p>
 *
 * @author Yun Jiao
 * @see FlexibleCaffeineCacheManager
 * @see CaffeineCacheManager
 */
@Slf4j
@RequiredArgsConstructor
public class CaffeineCacheManagerCreator implements CacheManagerCreator<CaffeineCacheManager> {
    private final CacheCaffeineProperties properties;
    private final Caffeine<Object, Object> caffeine;

    private CaffeineCacheManager instance;

    /**
     * 获取单例的{@link CaffeineCacheManager}实例。
     * <p>首次调用时会创建新实例，后续调用返回已创建的实例。</p>
     *
     * @return 缓存管理器实例（单例）
     */
    @Override
    public CaffeineCacheManager getInstance() {
        if (instance != null) {
            return instance;
        }

        synchronized (this) {
            if (instance != null) {
                return instance;
            }

            instance = newInstance();
        }

        return instance;
    }

    /**
     * 创建一个新的{@link CaffeineCacheManager}实例。
     * <p>使用{@link FlexibleCaffeineCacheManager}，并将配置的Caffeine实例设置到其中。</p>
     *
     * @return 新创建的缓存管理器实例
     */
    @Override
    public CaffeineCacheManager newInstance() {
        FlexibleCaffeineCacheManager caffeineCacheManager = new FlexibleCaffeineCacheManager(properties);
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }

    @Override
    public Class<CaffeineCacheManager> getCacheManagerClass() {
        return CaffeineCacheManager.class;
    }

}
