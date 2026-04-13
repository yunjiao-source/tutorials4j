package tutorials4j.framework.autoconfigure.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.util.CollectionUtils;
import tutorials4j.framework.spring.redis.RedisUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 初始化缓存
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class InitialRedisCacheManagerBuilderCustomizer implements RedisCacheManagerBuilderCustomizer {
    @Autowired
    private InitialRedisCacheProperties properties;

    @Override
    public void customize(RedisCacheManager.RedisCacheManagerBuilder builder) {
        if (CollectionUtils.isEmpty(properties.getRedis())) {
            log.debug("Turorials4j |- 没有配置初始化缓存");
            return;
        }

        Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
        Map<String, CacheProperties.Redis> redisProps = properties.getRedis();
        redisProps.forEach((key, redisProp) -> {
            RedisCacheConfiguration redisCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                    .computePrefixWith(RedisUtils.cacheKeyPrefix());

            if (redisProp.getTimeToLive() != null) {
                redisCacheConfig = redisCacheConfig.entryTtl(redisProp.getTimeToLive());
            }

            if (Objects.equals(Boolean.TRUE, redisProp.isCacheNullValues())) {
                redisCacheConfig = redisCacheConfig.disableCachingNullValues();
            }

            if (Objects.equals(Boolean.TRUE, redisProp.isUseKeyPrefix())) {
                redisCacheConfig = redisCacheConfig.computePrefixWith(RedisUtils.cacheKeyPrefix(redisProp.getKeyPrefix()));
            }

            configMap.put(key, redisCacheConfig);

        });
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .computePrefixWith(RedisUtils.cacheKeyPrefix());
        builder.cacheDefaults(defaultCacheConfig).withInitialCacheConfigurations(configMap);

        log.debug("Turorials4j |- 成功初始化缓存[{}]", String.join(",", configMap.keySet()));
    }
}
