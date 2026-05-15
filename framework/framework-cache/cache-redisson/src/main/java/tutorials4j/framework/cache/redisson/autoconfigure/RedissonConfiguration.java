package tutorials4j.framework.cache.redisson.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.cache.core.lock.LockServiceFactory;
import tutorials4j.framework.cache.redisson.BlockRedissonLockService;
import tutorials4j.framework.cache.redisson.PrefixNameMapper;
import tutorials4j.framework.cache.redisson.RedissonLockableAspect;
import tutorials4j.framework.cache.redisson.ReentrantRedissonLockService;

/**
 * Redisson 配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class RedissonConfiguration {
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
  BlockRedissonLockService BlockRedissonLock(RedissonClient redissonClient) {
    log.debug("[CACHE-REDISSON] Block Redisson Lock");
    return new BlockRedissonLockService(redissonClient);
  }

  @Bean
  @ConditionalOnMissingBean
  ReentrantRedissonLockService reentrantRedissonLock(RedissonClient redissonClient) {
    log.debug("[CACHE-REDISSON] Reentrant Redisson Lock");
    return new ReentrantRedissonLockService(redissonClient);
  }

  @Bean
  @ConditionalOnMissingBean
  RedissonLockableAspect redissonLockableAspect(LockServiceFactory lockServiceFactory) {
    log.debug("[CACHE-REDISSON] Redisson Lockable Aspect");
    return new RedissonLockableAspect(lockServiceFactory);
  }
}
