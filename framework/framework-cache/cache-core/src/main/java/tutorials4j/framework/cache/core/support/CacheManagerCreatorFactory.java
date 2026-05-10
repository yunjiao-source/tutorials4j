package tutorials4j.framework.cache.core.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import tutorials4j.framework.cache.core.exception.CacheManagerCreatorNotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存管理器创建器工厂。
 * <p>
 * 单例工厂，用于根据不同的缓存类型（多级缓存、Redis、Caffeine）获取对应的 {@link CacheManagerCreator}，
 * 并通过该创建器获取指定名称的 {@link Cache} 实例。工厂初始化时需通过 {@link #setCacheManagerCreators(List)} 注册所有可用的创建器。
 * </p>
 *
 * @author Yun Jiao
 */
@Slf4j
public class CacheManagerCreatorFactory {
    private final Map<String, CacheManagerCreator<?>> cacheManagerCreators = new ConcurrentHashMap<>();

    private CacheManagerCreatorFactory() {
    }

    public final static CacheManagerCreatorFactory INSTANCE = new CacheManagerCreatorFactory();

    public void setCacheManagerCreators(Map<String, CacheManagerCreator<?>> cacheManagerCreators) {
        this.cacheManagerCreators.putAll(cacheManagerCreators);
    }

    public Map<String, CacheManagerCreator<?>> getCacheManagerCreators() {
        return Collections.unmodifiableMap(cacheManagerCreators);
    }

    public CacheManagerCreator<?> getCacheManagerCreator(String category) {
        return cacheManagerCreators.get(category);
    }

    public CacheManagerCreator<?> getCacheManagerCreator(CacheManagerCreatorCategory category) {
        return cacheManagerCreators.get(category.getCode());
    }

    /**
     * 根据缓存名称获取多级缓存（两级缓存）实例。
     *
     * @param cacheName 缓存名称
     * @return 多级缓存实例
     * @throws CacheManagerCreatorNotFoundException 如果未找到对应的缓存管理器创建器
     */
    public Cache getMultiLevelCache(String cacheName) {
        CacheManagerCreator<?> creator = getCacheManagerCreator(CacheManagerCreatorCategory.TENANT_MULTI_LEVEL);
        if (creator != null) {
            return creator.getInstance().getCache(cacheName);
        }

        creator = getCacheManagerCreator(CacheManagerCreatorCategory.MULTI_LEVEL);
        if (creator != null) {
            return creator.getInstance().getCache(cacheName);
        }

        throw new CacheManagerCreatorNotFoundException("获取两级缓存管理器失败");
    }

    /**
     * 根据缓存名称获取 Redis 缓存实例。
     *
     * @param cacheName 缓存名称
     * @return Redis 缓存实例
     * @throws CacheManagerCreatorNotFoundException 如果未找到对应的缓存管理器创建器
     */
    public Cache getRedisCache(String cacheName) {
        CacheManagerCreator<?> creator = getCacheManagerCreator(CacheManagerCreatorCategory.REDIS);
        if (creator != null) {
            return creator.getInstance().getCache(cacheName);
        }

        throw new CacheManagerCreatorNotFoundException("获取Redis缓存管理器失败");
    }

    /**
     * 根据缓存名称获取 Caffeine 本地缓存实例。
     * <p>
     * 优先获取支持租户的 Caffeine 缓存管理器，若不存在则获取普通 Caffeine 缓存管理器。
     * </p>
     *
     * @param cacheName 缓存名称
     * @return Caffeine 缓存实例
     * @throws CacheManagerCreatorNotFoundException 如果未找到任何本地缓存管理器创建器
     */
    public Cache getCaffeineCache(String cacheName) {
        // 先获取支持租户的
        CacheManagerCreator<?> creator = getCacheManagerCreator(CacheManagerCreatorCategory.TENANT_CAFFEINE);
        if (creator != null) {
            return creator.getInstance().getCache(cacheName);
        }

        creator = getCacheManagerCreator(CacheManagerCreatorCategory.CAFFEINE);
        if (creator != null) {
            return creator.getInstance().getCache(cacheName);
        }

        throw new CacheManagerCreatorNotFoundException("获取本地缓存管理器失败");
    }
}
