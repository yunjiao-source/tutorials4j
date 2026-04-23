package tutorials4j.framework.cache.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.util.CollectionUtils;
import tutorials4j.framework.cache.core.properties.CacheRedisProperties;

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
            log.debug("Tutorials4j - Cache - Redis |- 没有配置命名缓存");
            return;
        }
        Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
        Map<String, CacheProperties.Redis> redisProps = properties.getNamedCaches();
        redisProps.forEach((key, redisProp) -> {
            // 独立配置覆盖默认配置
            RedisCacheConfiguration namedCacheConfiguration = fillConfiguration(builder.cacheDefaults(), redisProp);
            configMap.put(key, namedCacheConfiguration);

        });

        builder.withInitialCacheConfigurations(configMap);
        log.debug("Tutorials4j - Cache - Redis |- 成功初始化缓存[{}]", String.join(",", configMap.keySet()));
    }

    /**
     * 根据给定的 {@link CacheProperties.Redis} 属性填充 {@link RedisCacheConfiguration} 配置。
     *
     * @param configuration 原始的 {@link RedisCacheConfiguration} 实例，将基于它进行修改
     * @param prop          包含具体缓存配置的 {@link CacheProperties.Redis} 对象
     * @return 填充后的 {@link RedisCacheConfiguration} 实例
     */
    private RedisCacheConfiguration fillConfiguration(RedisCacheConfiguration configuration,
                                                      CacheProperties.Redis prop) {

        // 复制一份
        RedisCacheConfiguration copyConfig = configuration.computePrefixWith(configuration.getKeyPrefix());
        if (prop.getTimeToLive() != null) {
            copyConfig = copyConfig.entryTtl(prop.getTimeToLive());
        }

        if (!prop.isCacheNullValues()) {
            copyConfig = copyConfig.disableCachingNullValues();
        }

        if (prop.isUseKeyPrefix() && StringUtils.isNotBlank(prop.getKeyPrefix())) {
            copyConfig = copyConfig.computePrefixWith(RedisUtils.defaultCacheKeyPrefix(prop.getKeyPrefix()));
        }

        return copyConfig;
    }

}
