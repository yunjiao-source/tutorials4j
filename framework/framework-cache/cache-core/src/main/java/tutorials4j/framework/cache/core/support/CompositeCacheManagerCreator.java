package tutorials4j.framework.cache.core.support;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.CompositeCacheManager;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 组合缓存管理器创建器。
 *
 * <p>该创建器从 Spring 容器中获取所有 {@link CacheManagerSupplier} 实例，
 * 将其提供的 {@link CacheManager} 收集起来，构建一个 {@link CompositeCacheManager}。
 * 如果没有找到任何缓存管理器，则设置 {@code fallbackToNoOpCache} 为 {@code true}，
 * 使用 NoOp 缓存作为后备。
 *
 * @author Yun Jiao
 * @see CompositeCacheManager
 * @see CacheManagerSupplier
 */
@Slf4j
@RequiredArgsConstructor
public class CompositeCacheManagerCreator implements Supplier<CompositeCacheManager> {
    private final ObjectProvider<CacheManagerSupplier> cacheManagerSuppliers;

    /**
     * 创建并返回组合缓存管理器。
     *
     * <p>按顺序获取所有 {@link CacheManagerSupplier} 提供的 {@link CacheManager} 实例，
     * 若列表非空则设置到 {@link CompositeCacheManager} 中；
     * 若列表为空，则启用后备 NoOp 缓存策略，避免无可用缓存管理器导致错误。
     *
     * @return 配置好的 {@link CompositeCacheManager} 实例
     */
    @Override
    public CompositeCacheManager get() {
        CompositeCacheManager compositeCacheManager = new CompositeCacheManager();
        List<CacheManager> cacheManagers = cacheManagerSuppliers.orderedStream()
                .map(CacheManagerSupplier::get)
                .collect(Collectors.toList());
        if (ObjectUtils.isNotEmpty(cacheManagers)) {
            compositeCacheManager.setCacheManagers(cacheManagers);
        } else {
            // 防止没有缓存管理器注入
            compositeCacheManager.setFallbackToNoOpCache(true);
        }
        log.debug("Tutorials4j - Cache |- 组合缓存管理器[CompositeCacheManager]中组合实例信息:{}", cacheManagers);
        return compositeCacheManager;
    }
}
