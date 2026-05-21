package tutorials4j.framework.cache.redisson.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.cache.redisson.PrefixNameMapper;
import tutorials4j.framework.cache.redisson.lock.RedissonBlockLockService;
import tutorials4j.framework.cache.redisson.lock.RedissonBlockLockableAspect;
import tutorials4j.framework.cache.redisson.lock.RedissonReentrantLockService;
import tutorials4j.framework.cache.redisson.lock.RedissonReentrantLockableAspect;
import tutorials4j.framework.common.spring.content.SpelMethodBasedExpressionEvaluator;

/**
 * Redisson 配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class RedissonCacheConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.debug("[CACHE-REDISSON] Redisson Configuration");
  }

  @Bean
  @ConditionalOnMissingBean
  PrefixNameMapper prefixNameMapper() {
    log.debug("[CACHE-REDISSON] Prefix Name Mapper");
    return new PrefixNameMapper();
  }

  @Bean
  @ConditionalOnMissingBean
  RedissonAutoConfigurationCustomizer prefixNameRedissonConfigCustomizer(
      PrefixNameMapper prefixNameMapper) {
    log.debug("[CACHE-REDISSON] Prefix Name Redisson Config Customizer");
    return config -> config.setNameMapper(prefixNameMapper);
  }

  @Bean
  @ConditionalOnMissingBean
  RedissonBlockLockService blockRedissonLock(RedissonClient redissonClient) {
    log.debug("[CACHE-REDISSON] Redisson Block Lock Service");
    return new RedissonBlockLockService(redissonClient);
  }

  @Bean
  @ConditionalOnMissingBean
  RedissonReentrantLockService reentrantRedissonLock(RedissonClient redissonClient) {
    log.debug("[CACHE-REDISSON] Reentrant Redisson Lock");
    return new RedissonReentrantLockService(redissonClient);
  }

  @Bean
  @ConditionalOnMissingBean
  RedissonBlockLockableAspect blockRedissonLockableAspect(
      SpelMethodBasedExpressionEvaluator spelMethodBasedExpressionEvaluator,
      RedissonBlockLockService redissonBlockLockService) {
    log.debug("[CACHE-REDISSON] Redisson Block Lockable Aspect");
    return new RedissonBlockLockableAspect(
        spelMethodBasedExpressionEvaluator, redissonBlockLockService);
  }

  @Bean
  @ConditionalOnMissingBean
  RedissonReentrantLockableAspect redissonReentrantLockableAspect(
      SpelMethodBasedExpressionEvaluator spelMethodBasedExpressionEvaluator,
      RedissonReentrantLockService redissonReentrantLockService) {
    log.debug("[CACHE-REDISSON] Redisson Reentrant Lockable Aspect");
    return new RedissonReentrantLockableAspect(
        spelMethodBasedExpressionEvaluator, redissonReentrantLockService);
  }
}
