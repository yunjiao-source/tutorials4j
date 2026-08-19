package tutorials4j.framework.cache.multi.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.cache.caffeine.CaffeineCacheManagerCreator;
import tutorials4j.framework.cache.multi.MultiLevelCache;
import tutorials4j.framework.cache.multi.MultiLevelCacheManager;
import tutorials4j.framework.cache.multi.MultiLevelCacheManagerCreator;
import tutorials4j.framework.cache.redis.RedisCacheManagerCreator;

/**
 * Spring 配置类，用于启用多级缓存（本地 Caffeine + 远程 Redis）支持。
 *
 * @author Yun Jiao
 * @see MultiLevelCacheManagerCreator
 * @see MultiLevelCacheManager
 * @see MultiLevelCache
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class MultiLevelCacheConfiguration {
  /** 初始化日志记录。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[CACHE-MULTI-LEVEL] Cache Multi Level Configuration");
  }

  /**
   * 注册多级缓存管理器创建器 Bean。
   *
   * @param caffeineCacheManagerCreator Caffeine 本地缓存管理器创建器
   * @param redisCacheManagerCreator Redis 远程缓存管理器创建器
   * @return 多级缓存管理器创建器实例
   */
  @Bean
  @ConditionalOnMissingBean
  MultiLevelCacheManagerCreator multiLevelCacheManagerCreator(
      CaffeineCacheManagerCreator caffeineCacheManagerCreator,
      RedisCacheManagerCreator redisCacheManagerCreator) {
    log.trace("[CACHE-MULTI-LEVEL] Multi Level Cache Manager Creator");

    return new MultiLevelCacheManagerCreator(caffeineCacheManagerCreator, redisCacheManagerCreator);
  }
}
