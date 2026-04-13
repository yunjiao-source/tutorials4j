package tutorials4j.framework.autoconfigure.redis;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.cache.RedisCacheManager;

/**
 * 初始化缓存配置
 *
 * @author Yun Jiao
 */
@Slf4j
@ConditionalOnClass({RedisCacheManager.class})
@EnableConfigurationProperties(InitialRedisCacheProperties.class)
@Import({InitialRedisCacheManagerBuilderCustomizer.class, InitialRedisCacheManagerCustomizer.class})
public class InitialRedisCacheConfig {
    @PostConstruct
    public void postConstruct() {
        log.debug("Turorials4j |- Initial Redis Cache Config");
    }

}
