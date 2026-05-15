package tutorials4j.framework.cache.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.cache.caffeine.autoconfigure.CaffeineConfiguration;
import tutorials4j.framework.cache.core.autoconfigure.CacheConfiguration;
import tutorials4j.framework.cache.multi.autoconfigure.MultiLevelConfiguration;
import tutorials4j.framework.cache.redis.autoconfigure.RedisConfiguration;
import tutorials4j.framework.cache.redisson.autoconfigure.RedissonConfiguration;

/**
 * 自动配置
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@Import({CacheConfiguration.class
        , RedisConfiguration.class
        , CaffeineConfiguration.class
        , MultiLevelConfiguration.class
        , RedissonConfiguration.class})
public class CacheAutoConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("[CACHE] Cache Auto Configuration");
    }

}
