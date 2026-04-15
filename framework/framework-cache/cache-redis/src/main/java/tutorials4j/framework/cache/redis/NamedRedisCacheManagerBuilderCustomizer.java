package tutorials4j.framework.cache.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 命名缓存管理器配置
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class NamedRedisCacheManagerBuilderCustomizer implements RedisCacheManagerBuilderCustomizer {
    private final NamedRedisCacheProperties properties;

    @Override
    public void customize(RedisCacheManager.RedisCacheManagerBuilder builder) {
        if (CollectionUtils.isEmpty(properties.getNamedRedisCaches())) {
            log.debug("Turorials4j |- 没有配置初始化缓存");
            return;
        }

        final RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .computePrefixWith(RedisUtils.tutorials4jCacheKeyPrefix());

        Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
        Map<String, CacheProperties.Redis> redisProps = properties.getNamedRedisCaches();
        redisProps.forEach((key, redisProp) -> {
            RedisCacheConfiguration redisCacheConfig = defaultCacheConfig;
            if (redisProp.getTimeToLive() != null) {
                redisCacheConfig = redisCacheConfig.entryTtl(redisProp.getTimeToLive());
            }

            if (Objects.equals(Boolean.TRUE, redisProp.isCacheNullValues())) {
                redisCacheConfig = redisCacheConfig.disableCachingNullValues();
            }

            if (Objects.equals(Boolean.TRUE, redisProp.isUseKeyPrefix())) {
                redisCacheConfig = redisCacheConfig.computePrefixWith(RedisUtils.tutorials4jCacheKeyPrefix(redisProp.getKeyPrefix()));
            }

            configMap.put(key, redisCacheConfig);

        });

        builder.cacheDefaults(defaultCacheConfig).withInitialCacheConfigurations(configMap);
        log.debug("Turorials4j |- 成功初始化缓存[{}]", String.join(",", configMap.keySet()));
    }
}
