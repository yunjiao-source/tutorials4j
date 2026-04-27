package tutorials4j.framework.cache.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import tutorials4j.framework.cache.core.properties.CacheCaffeineProperties;
import tutorials4j.framework.cache.core.properties.CaffeineOptions;

import java.util.Map;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
public class FlexibleCaffeineCacheManager extends CaffeineCacheManager {
    private final CacheCaffeineProperties properties;

    public FlexibleCaffeineCacheManager(CacheCaffeineProperties properties) {
        this.properties = properties;
        this.setAllowNullValues(properties.getAllowNullValues());
    }

    public FlexibleCaffeineCacheManager(CacheCaffeineProperties properties, String... cacheNames) {
        super(cacheNames);
        this.properties = properties;
        this.setAllowNullValues(properties.getAllowNullValues());
    }

    @Override
    protected Cache<Object, Object> createNativeCaffeineCache(String name) {
        Map<String, CaffeineOptions> optionsMap = properties.getNamedCaches();
        if (MapUtils.isNotEmpty(optionsMap)) {
            if (optionsMap.containsKey(name)) {
                CaffeineOptions caffeineOptions = optionsMap.get(name);
                // 使用默认配置
                caffeineOptions.mergeNullValue(properties);

                Caffeine<Object, Object> caffeine = Caffeine.newBuilder();
                CaffeineUtils.copyOption(caffeine, caffeineOptions);

                log.debug("Tutorials4j - Cache |- Caffeine缓存管理器初始化缓存: {}", name);
                return caffeine.build();
            }
        }

        return super.createNativeCaffeineCache(name);
    }
}
