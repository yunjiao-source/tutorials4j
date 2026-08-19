package tutorials4j.framework.tenant.cache.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.cache.caffeine.CaffeineCacheManagerCreator;
import tutorials4j.framework.cache.redis.RedisCacheManagerCreator;
import tutorials4j.framework.tenant.cache.TenantCaffeineCacheManagerCreator;
import tutorials4j.framework.tenant.cache.TenantMultiLevelCacheManagerCreator;

/**
 * 租户缓存自动配置：注册租户级 Caffeine 缓存管理器创建器与租户级多级缓存管理器创建器 Bean（容器中不存在时生效）。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class CacheTenantConfiguration {

  /** 初始化日志输出 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[TENANT-CACHE] Cache Configuration");
  }

  /**
   * 注册租户级 Caffeine 缓存管理器创建器 Bean（容器中不存在时生效）。
   *
   * @param caffeineCacheManagerCreator 底层 Caffeine 缓存管理器创建器
   * @return 租户级 Caffeine 缓存管理器创建器
   */
  @Bean
  @ConditionalOnMissingBean
  TenantCaffeineCacheManagerCreator tenantCaffeineCacheManagerCreator(
      CaffeineCacheManagerCreator caffeineCacheManagerCreator) {
    log.trace("[TENANT-CACHE] Tenant Caffeine Cache Manager Creator");

    return new TenantCaffeineCacheManagerCreator(caffeineCacheManagerCreator);
  }

  /**
   * 注册租户级多级缓存管理器创建器 Bean（容器中不存在时生效）。
   *
   * @param tenantCaffeineCacheManagerCreator 租户级 Caffeine 缓存管理器创建器
   * @param redisCacheManagerCreator Redis 缓存管理器创建器
   * @return 租户级多级缓存管理器创建器
   */
  @Bean
  @ConditionalOnMissingBean
  TenantMultiLevelCacheManagerCreator tenantMultiLevelCacheManagerCreator(
      TenantCaffeineCacheManagerCreator tenantCaffeineCacheManagerCreator,
      RedisCacheManagerCreator redisCacheManagerCreator) {
    log.trace("[TENANT-CACHE] Tenant Multi Level Cache Manager Creator");

    return new TenantMultiLevelCacheManagerCreator(
        tenantCaffeineCacheManagerCreator, redisCacheManagerCreator);
  }
}
