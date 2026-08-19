package tutorials4j.framework.cache.redisson.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.cache.core.properties.CacheProperties;
import tutorials4j.framework.cache.redisson.PrefixNameMapper;
import tutorials4j.framework.cache.redisson.lock.RedissonBlockLockService;
import tutorials4j.framework.cache.redisson.lock.RedissonBlockLockableAspect;
import tutorials4j.framework.cache.redisson.lock.RedissonReentrantLockService;
import tutorials4j.framework.cache.redisson.lock.RedissonReentrantLockableAspect;
import tutorials4j.framework.common.spring.content.SpelMethodBasedExpressionEvaluator;

/**
 * Redisson 缓存配置类，装配缓存键前缀名称映射器、Redisson 分布式锁服务及锁切面。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class RedissonCacheConfiguration {
  /** 初始化日志输出。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[CACHE-REDISSON] Redisson Configuration");
  }

  /**
   * 创建缓存键前缀名称映射器。
   *
   * @param properties 缓存配置属性
   * @return 前缀名称映射器
   */
  @Bean
  @ConditionalOnMissingBean
  PrefixNameMapper prefixNameMapper(CacheProperties properties) {
    log.trace("[CACHE-REDISSON] Prefix Name Mapper");
    return new PrefixNameMapper(properties.getRedissonCacheName());
  }

  /**
   * 创建 Redisson 自动配置定制器，为 Redisson 应用缓存键前缀名称映射。
   *
   * @param prefixNameMapper 前缀名称映射器
   * @return Redisson 自动配置定制器
   */
  @Bean
  @ConditionalOnMissingBean
  RedissonAutoConfigurationCustomizer prefixNameRedissonConfigCustomizer(
      PrefixNameMapper prefixNameMapper) {
    log.trace("[CACHE-REDISSON] Prefix Name Redisson Config Customizer");
    return config -> config.setNameMapper(prefixNameMapper);
  }

  /**
   * 创建基于 Redisson 的阻塞式分布式锁服务（单例）。
   *
   * @param redissonClient Redisson 客户端
   * @return 阻塞式分布式锁服务
   */
  @Bean
  @ConditionalOnMissingBean
  RedissonBlockLockService blockRedissonLock(RedissonClient redissonClient) {
    log.trace("[CACHE-REDISSON] Redisson Block Lock Service");
    RedissonBlockLockService.instance.setRedissonClient(redissonClient);
    return RedissonBlockLockService.instance;
  }

  /**
   * 创建基于 Redisson 的可重入分布式锁服务（单例）。
   *
   * @param redissonClient Redisson 客户端
   * @return 可重入分布式锁服务
   */
  @Bean
  @ConditionalOnMissingBean
  RedissonReentrantLockService reentrantRedissonLock(RedissonClient redissonClient) {
    log.trace("[CACHE-REDISSON] Reentrant Redisson Lock");
    RedissonReentrantLockService.instance.setRedissonClient(redissonClient);
    return RedissonReentrantLockService.instance;
  }

  /**
   * 创建基于 Redisson 的阻塞式分布式锁切面。
   *
   * @param spelMethodBasedExpressionEvaluator SpEL 表达式求值器
   * @param redissonBlockLockService 阻塞式分布式锁服务
   * @return 阻塞式分布式锁切面
   */
  @Bean
  @ConditionalOnMissingBean
  RedissonBlockLockableAspect blockRedissonLockableAspect(
      SpelMethodBasedExpressionEvaluator spelMethodBasedExpressionEvaluator,
      RedissonBlockLockService redissonBlockLockService) {
    log.trace("[CACHE-REDISSON] Redisson Block Lockable Aspect");
    return new RedissonBlockLockableAspect(
        spelMethodBasedExpressionEvaluator, redissonBlockLockService);
  }

  /**
   * 创建基于 Redisson 的可重入分布式锁切面。
   *
   * @param spelMethodBasedExpressionEvaluator SpEL 表达式求值器
   * @param redissonReentrantLockService 可重入分布式锁服务
   * @return 可重入分布式锁切面
   */
  @Bean
  @ConditionalOnMissingBean
  RedissonReentrantLockableAspect redissonReentrantLockableAspect(
      SpelMethodBasedExpressionEvaluator spelMethodBasedExpressionEvaluator,
      RedissonReentrantLockService redissonReentrantLockService) {
    log.trace("[CACHE-REDISSON] Redisson Reentrant Lockable Aspect");
    return new RedissonReentrantLockableAspect(
        spelMethodBasedExpressionEvaluator, redissonReentrantLockService);
  }
}
