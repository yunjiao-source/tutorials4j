package tutorials4j.framework.cache.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import tutorials4j.framework.cache.core.properties.NamedCacheOptions;
import tutorials4j.framework.cache.core.properties.NamedCacheProperties;

import java.util.Map;
import java.util.Objects;

/**
 * 灵活的Caffeine缓存管理器，扩展自{@link CaffeineCacheManager}。
 * <p>支持为每个缓存名称单独配置参数，通过{@link NamedCacheProperties#getCaches()}获取特定缓存的配置。
 * 如果某个缓存名称存在单独配置，则使用该配置创建对应的原生Caffeine缓存；否则回退到父类的默认创建逻辑。</p>
 *
 * @author Yun Jiao
 * @see CaffeineCacheManager
 * @see NamedCacheProperties
 */
@Slf4j
public class FlexibleCaffeineCacheManager extends CaffeineCacheManager {
    private final NamedCacheProperties properties;

    /**
     * 使用给定的全局配置构造一个缓存管理器。
     *
     * @param properties 全局Caffeine缓存配置属性
     */
    public FlexibleCaffeineCacheManager(NamedCacheProperties properties) {
        this.properties = properties;
        this.setAllowNullValues(Objects.equals(properties.getDefaults().getCacheNullValues(), Boolean.TRUE));
    }

    /**
     * 使用给定的全局配置和一组缓存名称构造缓存管理器。
     *
     * @param properties 全局Caffeine缓存配置属性
     * @param cacheNames 初始化的缓存名称列表
     */
    public FlexibleCaffeineCacheManager(NamedCacheProperties properties, String... cacheNames) {
        super(cacheNames);
        this.properties = properties;
        this.setAllowNullValues(Objects.equals(properties.getDefaults().getCacheNullValues(), Boolean.TRUE));
    }

    /**
     * 创建指定缓存名称的原生Caffeine缓存对象。
     * <p>若{@link NamedCacheProperties#getCaches()}中包含该缓存名称的单独配置，
     * 则根据该配置新建一个{@link Caffeine}实例并构建缓存；否则调用父类方法创建默认缓存。</p>
     *
     * @param name 缓存名称
     * @return 原生Caffeine缓存实例
     */
    @Override
    protected Cache<Object, Object> createNativeCaffeineCache(String name) {
        Map<String, NamedCacheOptions> optionsMap = properties.getCaches();
        if (MapUtils.isNotEmpty(optionsMap)) {
            if (optionsMap.containsKey(name)) {
                NamedCacheOptions options = optionsMap.get(name);
                // 使用默认配置
                options.applyDefaults(properties.getDefaults());

                Caffeine<Object, Object> caffeine = Caffeine.newBuilder();
                CaffeineUtils.copyOption(caffeine, options);

                if (log.isDebugEnabled()) {
                    log.debug("[CACHE-CAFFEINE] Caffeine缓存管理器初始化缓存: {}", name);
                }
                return caffeine.build();
            }
        }

        return super.createNativeCaffeineCache(name);
    }
}
