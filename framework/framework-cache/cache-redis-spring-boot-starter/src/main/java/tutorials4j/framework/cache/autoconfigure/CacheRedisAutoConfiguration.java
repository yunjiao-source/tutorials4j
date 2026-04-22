package tutorials4j.framework.cache.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.cache.core.autoconfigure.CacheCoreConfiguration;
import tutorials4j.framework.cache.redis.NamedCacheConfiguration;

/**
 * 自动配置
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@Import({CacheCoreConfiguration.class, NamedCacheConfiguration.class})
public class CacheRedisAutoConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j |- Named Cache Auto Configuration");
    }

}
