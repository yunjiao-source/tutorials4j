package tutorials4j.framework.cache.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.util.CollectionUtils;
import tutorials4j.framework.cache.core.properties.CacheRedisProperties;
import tutorials4j.framework.cache.core.properties.RedisOptions;

import java.util.HashMap;
import java.util.Map;

/**
 * 命名缓存管理器构建器定制器。
 * <p>
 * 该类实现 {@link RedisCacheManagerBuilderCustomizer} 接口，用于在构建 {@link RedisCacheManager} 时，
 * 根据 {@link CacheRedisProperties} 中配置的命名缓存（named caches）为每个缓存独立设置
 * {@link RedisCacheConfiguration} 配置（如 TTL、是否允许缓存 null 值、键前缀等）。
 * </p>
 * <p>
 * 如果未配置任何命名缓存，则不进行任何自定义操作。
 * </p>
 *
 * @author Yun Jiao
 * @see RedisCacheManagerBuilderCustomizer
 * @see CacheRedisProperties
 */
@Slf4j
@RequiredArgsConstructor
public class NamedRedisCacheManagerBuilderCustomizer implements RedisCacheManagerBuilderCustomizer {

    /**
     * Redis 缓存相关配置属性，包含默认配置和命名缓存配置。
     */
    private final CacheRedisProperties properties;

    /**
     * 自定义 {@link RedisCacheManager.RedisCacheManagerBuilder}。
     * <p>
     * 为每个配置的命名缓存生成独立的 {@link RedisCacheConfiguration}，
     * 并将其添加到构建器中作为初始缓存配置。
     * </p>
     * <p>
     * 同时会设置全局默认配置，该默认配置由 {@code properties} 中的通用属性决定。
     * </p>
     *
     * @param builder 要自定义的 {@link RedisCacheManager.RedisCacheManagerBuilder} 实例
     */
    @Override
    public void customize(RedisCacheManager.RedisCacheManagerBuilder builder) {
        if (CollectionUtils.isEmpty(properties.getNamedCaches())) {
            return;
        }

        RedisCacheConfiguration defaultConfig = builder.cacheDefaults();
        Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
        Map<String, RedisOptions> redisOption = properties.getNamedCaches();

        redisOption.forEach((key, redisProp) -> {
            // 独立配置覆盖默认配置
            RedisCacheConfiguration namedCacheConfiguration = RedisUtils.fillConfiguration(defaultConfig, redisProp);
            configMap.put(key, namedCacheConfiguration);

        });

        if (properties.isEnableStatistics()) {
            builder.enableStatistics();
        }
        builder.cacheDefaults(defaultConfig).withInitialCacheConfigurations(configMap);
        log.debug("Tutorials4j - Cache |- Redis缓存管理器初始化缓存：{}", String.join(",", configMap.keySet()));
    }


}
