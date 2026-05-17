package tutorials4j.framework.cache.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.cache.caffeine.autoconfigure.CaffeineConfiguration;
import tutorials4j.framework.cache.core.autoconfigure.CacheConfiguration;
import tutorials4j.framework.cache.multi.autoconfigure.MultiLevelCacheConfiguration;
import tutorials4j.framework.cache.redis.autoconfigure.RedisCacheConfiguration;
import tutorials4j.framework.cache.redisson.autoconfigure.RedissonCacheConfiguration;

/**
 * 自动配置
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@Import({
  CacheConfiguration.class,
  RedisCacheConfiguration.class,
  CaffeineConfiguration.class,
  MultiLevelCacheConfiguration.class,
  RedissonCacheConfiguration.class
})
public class CacheAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.debug("[CACHE] Cache Auto Configuration");
  }
}
