package tutorials4j.framework.cache.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.cache.caffeine.CacheCaffeineConfiguration;
import tutorials4j.framework.cache.core.autoconfigure.CacheCoreConfiguration;
import tutorials4j.framework.cache.redis.autoconfigure.CacheRedisConfiguration;

/**
 * 自动配置
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@Import({CacheCoreConfiguration.class, CacheRedisConfiguration.class, CacheCaffeineConfiguration.class})
public class CacheAutoConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j - Cache |- Cache Auto Configuration");
    }

}
