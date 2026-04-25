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
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class CompositeCacheManagerCreator implements Supplier<CompositeCacheManager> {
    private final ObjectProvider<CacheManagerSupplier> cacheManagerSuppliers;

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
