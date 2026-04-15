package tutorials4j.framework.cache.redis;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 命名缓存配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(NamedRedisCacheProperties.class)
public class NamedRedisCacheConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j |- Named Redis Cache Configuration");
    }

    @Bean
    NamedRedisCacheManagerBuilderCustomizer namedRedisCacheManagerBuilderCustomizer(NamedRedisCacheProperties properties) {
        log.debug("Tutorials4j |- Named Redis Cache Manager Builder Customizer");
        return new NamedRedisCacheManagerBuilderCustomizer(properties);
    }

    @Bean
    NamedRedisCacheManagerCustomizer namedRedisCacheManagerCustomizer() {
        log.debug("Tutorials4j |- Named Redis Cache Manager Customizer");
        return new NamedRedisCacheManagerCustomizer();
    }
}
