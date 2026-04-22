package tutorials4j.framework.cache.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.util.CollectionUtils;
import tutorials4j.framework.cache.core.CacheUtils;
import tutorials4j.framework.cache.core.properties.CachesProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * 命名缓存管理器配置
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class NamedCacheManagerBuilderCustomizer implements RedisCacheManagerBuilderCustomizer {
    private final CachesProperties.RedisCacheOptions options;

    @Override
    public void customize(RedisCacheManager.RedisCacheManagerBuilder builder) {
        if (CollectionUtils.isEmpty(options.getNamedCaches())) {
            log.debug("Tutorials4j |- 没有配置初始化缓存");
            return;
        }

        Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
        Map<String, CacheProperties.Redis> redisProps = options.getNamedCaches();
        redisProps.forEach((key, redisProp) -> {
            RedisCacheConfiguration defaultCacheConfig = fillConfiguration(RedisCacheConfiguration.defaultCacheConfig(), options);

            // 独立配置
            defaultCacheConfig = fillConfiguration(defaultCacheConfig, redisProp);
            configMap.put(key, defaultCacheConfig);

        });

        final RedisCacheConfiguration defaultCacheConfig = fillConfiguration(RedisCacheConfiguration.defaultCacheConfig(), options);
        builder.cacheDefaults(defaultCacheConfig).withInitialCacheConfigurations(configMap);
        log.debug("Tutorials4j |- 成功初始化缓存[{}]", String.join(",", configMap.keySet()));
    }

    private RedisCacheConfiguration fillConfiguration(RedisCacheConfiguration configuration,
                                                      CacheProperties.Redis prop) {
        // 默认前缀
        configuration = configuration.prefixCacheNameWith(CacheUtils.cacheNamePrefix());

        if (prop.getTimeToLive() != null) {
            configuration = configuration.entryTtl(prop.getTimeToLive());
        }

        if (!prop.isCacheNullValues()) {
            configuration = configuration.disableCachingNullValues();
        }

        if (prop.isUseKeyPrefix() && StringUtils.isNotBlank(prop.getKeyPrefix())) {
            configuration = configuration.prefixCacheNameWith(CacheUtils.cacheName(prop.getKeyPrefix()));
        }

        return configuration;
    }

}
