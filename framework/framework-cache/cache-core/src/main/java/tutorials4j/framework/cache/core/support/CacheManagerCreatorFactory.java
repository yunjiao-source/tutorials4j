package tutorials4j.framework.cache.core.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import tutorials4j.framework.cache.core.exception.CacheManagerInstanceNotFoundException;
import tutorials4j.framework.common.core.DefaultConsts;

import java.util.*;

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
    private final List<CacheManagerCreator<?>> cacheManagerCreators = new ArrayList<>();

    private CacheManagerCreatorFactory() {
    }

    public final static CacheManagerCreatorFactory INSTANCE = new CacheManagerCreatorFactory();

    public void setCacheManagerCreators(List<CacheManagerCreator<?>> cacheManagerCreators) {
        this.cacheManagerCreators.addAll(cacheManagerCreators);
    }

    public List<CacheManagerCreator<?>> getCacheManagerCreators() {
        return Collections.unmodifiableList(cacheManagerCreators);
    }

    /**
     * 根据缓存名称获取多级缓存（两级缓存）实例。
     *
     * @param cacheName 缓存名称
     * @return 多级缓存实例
     * @throws CacheManagerInstanceNotFoundException 如果未找到对应的缓存管理器创建器
     */
    public Cache getMultiLevelCache(String cacheName) {
        Optional<CacheManagerCreator<?>> creator = getCacheManager(DefaultConsts.CLASS_MULTI_LEVEL_CACHE_MANAGER_CREATOR);
        if (creator.isPresent()) {
            return creator.get().getInstance().getCache(cacheName);
        }

        creator = getCacheManager(DefaultConsts.CLASS_MULTI_LEVEL_CACHE_MANAGER_CREATOR);
        if (creator.isPresent()) {
            return creator.get().getInstance().getCache(cacheName);
        }

        throw new CacheManagerInstanceNotFoundException("获取两级缓存管理器失败");
    }

    /**
     * 根据缓存名称获取 Redis 缓存实例。
     *
     * @param cacheName 缓存名称
     * @return Redis 缓存实例
     * @throws CacheManagerInstanceNotFoundException 如果未找到对应的缓存管理器创建器
     */
    public Cache getRedisCache(String cacheName) {
        Optional<CacheManagerCreator<?>> creator = getCacheManager(DefaultConsts.CLASS_REDIS_CACHE_MANAGER_CREATOR);
        if (creator.isPresent()) {
            return creator.get().getInstance().getCache(cacheName);
        }

        throw new CacheManagerInstanceNotFoundException("获取Redis缓存管理器失败");
    }

    /**
     * 根据缓存名称获取 Caffeine 本地缓存实例。
     * <p>
     * 优先获取支持租户的 Caffeine 缓存管理器，若不存在则获取普通 Caffeine 缓存管理器。
     * </p>
     *
     * @param cacheName 缓存名称
     * @return Caffeine 缓存实例
     * @throws CacheManagerInstanceNotFoundException 如果未找到任何本地缓存管理器创建器
     */
    public Cache getCaffeineCache(String cacheName) {
        // 先获取支持租户的
        Optional<CacheManagerCreator<?>> creator = getCacheManager(DefaultConsts.CLASS_CAFFEINE_CACHE_MANAGER_CREATOR);
        if (creator.isPresent()) {
            return creator.get().getInstance().getCache(cacheName);
        }

        creator = getCacheManager(DefaultConsts.CLASS_TENANT_CAFFEINE_CACHE_MANAGER_CREATOR);
        if (creator.isPresent()) {
            return creator.get().getInstance().getCache(cacheName);
        }

        throw new CacheManagerInstanceNotFoundException("获取本地缓存管理器失败");
    }

    /**
     * 根据类名获取对应的缓存管理器创建器（可选）。
     *
     * @param className 创建器的全限定类名
     * @return 创建器 Optional，若类不存在或未注册则返回空
     */
    private Optional<CacheManagerCreator<?>> getCacheManager(String className) {
        try {
            Class<?> cacheManagerClass = Class.forName(className);
            return getCacheManager(cacheManagerClass);
        } catch (ClassNotFoundException e) {
            log.debug(e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * 根据类对象获取对应的缓存管理器创建器（可选）。
     *
     * @param cacheManagerClass 创建器的类对象
     * @return 创建器 Optional，若未注册则返回空
     */
    private Optional<CacheManagerCreator<?>> getCacheManager(Class<?> cacheManagerClass) {
        return cacheManagerCreators.stream().filter(e -> Objects.equals(e.getClass(), cacheManagerClass)).findFirst();
    }
}
