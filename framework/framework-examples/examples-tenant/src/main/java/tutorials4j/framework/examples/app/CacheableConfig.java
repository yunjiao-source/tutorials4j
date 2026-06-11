package tutorials4j.framework.examples.app;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import tutorials4j.framework.cache.caffeine.CaffeineCacheManagerCreator;
import tutorials4j.framework.cache.redis.RedisCacheManagerCreator;
import tutorials4j.framework.tenant.cache.TenantCaffeineCacheManagerCreator;
import tutorials4j.framework.tenant.cache.TenantMultiLevelCacheManagerCreator;

/**
 * 缓存配置
 *
 * @author Yun Jiao
 */
@EnableCaching
@Configuration
@Profile("cache")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.cache"})
public class CacheableConfig {

  /**
   * 支持多种缓存管理器
   *
   * @param redisCacheManagerCreator
   * @param tenantMultiLevelCacheManagerCreator
   * @param tenantCaffeineCacheManagerCreator
   * @param caffeineCacheManagerCreator
   * @return
   */
  @Bean
  public CacheManager cacheManager(
      RedisCacheManagerCreator redisCacheManagerCreator,
      TenantMultiLevelCacheManagerCreator tenantMultiLevelCacheManagerCreator,
      TenantCaffeineCacheManagerCreator tenantCaffeineCacheManagerCreator,
      CaffeineCacheManagerCreator caffeineCacheManagerCreator) {
    return redisCacheManagerCreator.getInstance();
    // return tenantMultiLevelCacheManagerCreator.getInstance();
    // return tenantCaffeineCacheManagerCreator.getInstance();

    // 这个不支持租户
    // return caffeineCacheManagerCreator.getInstance();
  }
}
