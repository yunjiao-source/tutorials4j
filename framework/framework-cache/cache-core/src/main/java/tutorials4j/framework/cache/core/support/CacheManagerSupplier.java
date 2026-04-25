package tutorials4j.framework.cache.core.support;

import org.springframework.cache.CacheManager;

import java.util.function.Supplier;

/**
 * {@link CacheManager} 的供应商接口。
 *
 * <p>该接口继承自 {@link Supplier}，专门用于提供 {@link CacheManager} 实例。
 * 通常由不同的缓存实现（如 Redis、Caffeine 等）分别实现，以便 {@link CompositeCacheManagerCreator}
 * 统一收集并构建组合缓存管理器。
 *
 * @author Yun Jiao
 * @see CompositeCacheManagerCreator
 */
public interface CacheManagerSupplier extends Supplier<CacheManager> {
}
