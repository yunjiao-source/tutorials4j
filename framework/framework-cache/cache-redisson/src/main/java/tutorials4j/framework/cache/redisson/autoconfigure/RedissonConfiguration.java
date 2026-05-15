package tutorials4j.framework.cache.redisson.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.cache.redisson.BlockRedissonLock;
import tutorials4j.framework.cache.redisson.PrefixNameMapper;
import tutorials4j.framework.cache.redisson.ReentrantRedissonLock;

/**
 * Redisson 配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class RedissonConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("[CACHE-REDISSON] Redisson Configuration");
    }

    @Bean
    @ConditionalOnMissingBean
    PrefixNameMapper prefixNameMapper() {
        log.debug("[CACHE-REDISSON] Prefix Name Mapper");
        return new PrefixNameMapper();
    }

    @Bean
    @ConditionalOnMissingBean
    RedissonAutoConfigurationCustomizer prefixNameRedissonConfigCustomizer(PrefixNameMapper prefixNameMapper) {
        log.debug("[CACHE-REDISSON] Prefix Name Redisson Config Customizer");
        return config -> config.setNameMapper(prefixNameMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    BlockRedissonLock BlockRedissonLock(RedissonClient redissonClient) {
        log.debug("[CACHE-REDISSON] Block Redisson Lock");
        return new BlockRedissonLock(redissonClient);
    }

    @Bean
    @ConditionalOnMissingBean
    ReentrantRedissonLock reentrantRedissonLock(RedissonClient redissonClient) {
        log.debug("[CACHE-REDISSON] Reentrant Redisson Lock");
        return new ReentrantRedissonLock(redissonClient);
    }
}
