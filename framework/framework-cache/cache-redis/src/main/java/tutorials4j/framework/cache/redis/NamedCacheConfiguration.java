package tutorials4j.framework.cache.redis;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.cache.core.properties.CachesProperties;

/**
 * 命名缓存配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class NamedCacheConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j |- Named Cache Configuration");
    }

    @Bean
    NamedCacheManagerBuilderCustomizer namedRedisCacheManagerBuilderCustomizer(CachesProperties properties) {
        log.debug("Tutorials4j |- Named Redis Cache Manager Builder Customizer");
        return new NamedCacheManagerBuilderCustomizer(properties.getRedis());
    }

    @Bean
    NamedCacheManagerCustomizer namedRedisCacheManagerCustomizer() {
        log.debug("Tutorials4j |- Named Redis Cache Manager Customizer");
        return new NamedCacheManagerCustomizer();
    }
}
