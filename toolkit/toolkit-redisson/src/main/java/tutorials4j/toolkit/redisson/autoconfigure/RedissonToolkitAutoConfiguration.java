package tutorials4j.toolkit.redisson.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import tutorials4j.toolkit.core.context.SpelMethodBasedExpressionEvaluator;
import tutorials4j.toolkit.redisson.lock.RedissonBlockLockService;
import tutorials4j.toolkit.redisson.lock.RedissonBlockLockableAspect;
import tutorials4j.toolkit.redisson.lock.RedissonReentrantLockService;
import tutorials4j.toolkit.redisson.lock.RedissonReentrantLockableAspect;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
public class RedissonToolkitAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[TOOLKIT-REDISSON] Redisson Toolkit Auto Configuration");
  }

  @Bean
  RedissonBlockLockService redissonBlockLockService(RedissonClient redissonClient) {
    log.trace("[TOOLKIT-REDISSON] Redisson Block Lock Service");
    RedissonBlockLockService.instance.setRedissonClient(redissonClient);
    return RedissonBlockLockService.instance;
  }

  @Bean
  RedissonReentrantLockService redissonReentrantLockService(RedissonClient redissonClient) {
    log.trace("[TOOLKIT-REDISSON] Redisson Reentrant Lock Service");
    RedissonReentrantLockService.instance.setRedissonClient(redissonClient);
    return RedissonReentrantLockService.instance;
  }

  @Bean
  RedissonBlockLockableAspect redissonBlockLockableAspect(
      SpelMethodBasedExpressionEvaluator spelMethodBasedExpressionEvaluator,
      RedissonBlockLockService redissonBlockLockService) {
    log.trace("[TOOLKIT-REDISSON] Redisson Block Lockable Aspect");
    return new RedissonBlockLockableAspect(
        spelMethodBasedExpressionEvaluator, redissonBlockLockService);
  }

  @Bean
  RedissonReentrantLockableAspect redissonReentrantLockableAspect(
      SpelMethodBasedExpressionEvaluator spelMethodBasedExpressionEvaluator,
      RedissonReentrantLockService redissonReentrantLockService) {
    log.trace("[TOOLKIT-REDISSON] Redisson Reentrant Lockable Aspect");
    return new RedissonReentrantLockableAspect(
        spelMethodBasedExpressionEvaluator, redissonReentrantLockService);
  }
}
