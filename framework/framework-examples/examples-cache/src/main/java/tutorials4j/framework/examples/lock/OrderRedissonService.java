package tutorials4j.framework.examples.lock;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tutorials4j.framework.cache.redisson.lock.RedissonBlockLockable;
import tutorials4j.framework.cache.redisson.lock.RedissonReentrantLockable;

/**
 * 订单 Redisson 锁示例服务。
 *
 * <p>通过 {@code @RedissonBlockLockable} 与 {@code @RedissonReentrantLockable} 注解演示 Redisson
 * 分布式锁，包括阻塞锁、可重入锁以及是否设置过期时间等组合场景。
 *
 * @author Yun Jiao
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderRedissonService {
  /**
   * 无过期时间的 Redisson 阻塞锁示例：锁在业务执行期间不会自动过期。
   *
   * @param orderId 订单编号
   */
  @RedissonBlockLockable(key = "#root.args[0]", prefix = "order:")
  public void blockNonExpireTime(String orderId) {
    log.info("blockNonExpireTime - {} - {}", Thread.currentThread().getName(), orderId);
    int time = sleep();
    log.info(
        "blockNonExpireTime - {} - {}, 时长：{}", Thread.currentThread().getName(), orderId, time);
  }

  /**
   * 带过期时间的 Redisson 阻塞锁示例：锁在指定过期时间后自动释放。
   *
   * @param orderId 订单编号
   */
  @RedissonBlockLockable(key = "#root.args[0]", prefix = "order:", expireTime = 3000)
  public void blockExpireTime(String orderId) {
    log.info("blockExpireTime - {} - {}", Thread.currentThread().getName(), orderId);
    int time = sleep();
    log.info("blockExpireTime - {} - {}, 时长：{}", Thread.currentThread().getName(), orderId, time);
  }

  /**
   * 无过期时间的 Redisson 可重入锁示例：同一线程可重复获取锁。
   *
   * @param orderId 订单编号
   */
  @RedissonReentrantLockable(key = "#root.args[0]", prefix = "order:")
  public void reentrantNonExpireTime(String orderId) {
    log.info("reentrantNonExpireTime - {} - {}", Thread.currentThread().getName(), orderId);
    int time = sleep();
    log.info(
        "reentrantNonExpireTime - {} - {}, 时长：{}", Thread.currentThread().getName(), orderId, time);
  }

  /**
   * 带过期时间的 Redisson 可重入锁示例：锁在指定过期时间后自动释放。
   *
   * @param orderId 订单编号
   */
  @RedissonReentrantLockable(key = "#root.args[0]", prefix = "order:", expireTime = 4000)
  public void reentrantExpireTime(String orderId) {
    log.info("reentrantFixedLease - {} - {}", Thread.currentThread().getName(), orderId);
    int time = sleep();
    log.info(
        "reentrantFixedLease - {} - {}, 时长：{}", Thread.currentThread().getName(), orderId, time);
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
