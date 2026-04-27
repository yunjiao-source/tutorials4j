package tutorials4j.framework.cache.caffeine;

import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.cache.core.properties.CacheCaffeineProperties;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class CacheCaffeineConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j - Cache |- Cache Caffeine Configuration");
    }

    @Bean
    Caffeine<Object, Object> caffeine(CacheCaffeineProperties properties) {
        log.debug("Tutorials4j - Cache |- Caffeine");

        Caffeine<Object, Object> caffeine = Caffeine.newBuilder();
        CaffeineUtils.copyOption(caffeine, properties);
        return caffeine;
    }

    @Bean
    @ConditionalOnMissingBean(CaffeineCacheManagerCreator.class)
    CaffeineCacheManagerCreator caffeineCacheManagerCreator(Caffeine<Object, Object> caffeine,
                                              CacheCaffeineProperties properties) {
        log.debug("Tutorials4j - Cache |- Caffeine Cache Manager Creator");

        return new CaffeineCacheManagerCreator(properties, caffeine);
    }

}
