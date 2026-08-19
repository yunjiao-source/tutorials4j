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
 * 缓存示例配置类，启用 Spring 缓存并注册多种缓存管理器（Redis、多级缓存、Caffeine 等）。
 *
 * <p>仅在 {@code cache} Profile 下生效，并扫描 {@code tutorials4j.framework.examples.cache} 包中的缓存相关组件。
 *
 * @author Yun Jiao
 */
@EnableCaching
@Configuration
@Profile("cache")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.cache"})
public class CacheableConfig {

  /**
   * 创建并返回当前使用的缓存管理器，默认使用 Redis 缓存管理器实例。
   *
   * <p>被注释掉的代码展示了多级缓存、租户 Caffeine 缓存等备选方案，可按需切换。
   *
   * @param redisCacheManagerCreator Redis 缓存管理器创建器
   * @param tenantMultiLevelCacheManagerCreator 租户多级缓存管理器创建器
   * @param tenantCaffeineCacheManagerCreator 租户 Caffeine 缓存管理器创建器
   * @param caffeineCacheManagerCreator Caffeine 缓存管理器创建器（不支持租户）
   * @return 实际使用的缓存管理器
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
