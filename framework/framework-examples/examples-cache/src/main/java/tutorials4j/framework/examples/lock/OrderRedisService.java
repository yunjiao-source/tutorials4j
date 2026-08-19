package tutorials4j.framework.examples.lock;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tutorials4j.framework.cache.redis.lock.RedisLockable;

/**
 * 订单 Redis 锁示例服务。
 *
 * <p>通过 {@code @RedisLockable} 注解演示基于 Redis 的分布式锁，包括无过期时间与带过期时间两种加锁模式。
 *
 * @author Yun Jiao
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderRedisService {
  /**
   * 无过期时间的 Redis 锁示例：锁在业务执行期间不会自动过期。
   *
   * @param orderId 订单编号
   */
  @RedisLockable(key = "#root.args[0]", prefix = "order:")
  public void nonExpireTime(String orderId) {
    log.info("nonExpireTime - {} - {}", Thread.currentThread().getName(), orderId);
    int time = sleep();
    log.info("nonExpireTime - {} - {}, 时长：{}", Thread.currentThread().getName(), orderId, time);
  }

  /**
   * 带过期时间的 Redis 锁示例：锁在指定过期时间后自动释放。
   *
   * @param orderId 订单编号
   */
  @RedisLockable(key = "#root.args[0]", prefix = "order:", expireTime = 3000)
  public void expireTime(String orderId) {
    log.info("expireTime - {} - {}", Thread.currentThread().getName(), orderId);
    int time = sleep();
    log.info("expireTime - {} - {}, 时长：{}", Thread.currentThread().getName(), orderId, time);
  }

  /**
   * 随机睡眠 0~9 秒以模拟业务处理耗时。
   *
   * @return 实际睡眠的秒数
   */
  private int sleep() {
    try {
      int seconds = ThreadLocalRandom.current().nextInt(10);
      TimeUnit.SECONDS.sleep(seconds);
      return seconds;
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    }
  }
}
