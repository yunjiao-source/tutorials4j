package tutorials4j.framework.cache.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.cache.caffeine.autoconfigure.CaffeineCacheConfiguration;
import tutorials4j.framework.cache.core.autoconfigure.CacheConfiguration;
import tutorials4j.framework.cache.multi.autoconfigure.MultiLevelCacheConfiguration;
import tutorials4j.framework.cache.redis.autoconfigure.RedisCacheConfiguration;
import tutorials4j.framework.cache.redisson.autoconfigure.RedissonCacheConfiguration;

/**
 * 缓存模块自动配置入口，导入缓存核心、Redis、Caffeine、多级缓存与 Redisson 配置。
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@Import({
  CacheConfiguration.class,
  RedisCacheConfiguration.class,
  CaffeineCacheConfiguration.class,
  MultiLevelCacheConfiguration.class,
  RedissonCacheConfiguration.class
})
public class CacheAutoConfiguration {
  /** 初始化日志输出。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[CACHE] Cache Auto Configuration");
  }
}
