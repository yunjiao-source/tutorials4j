package tutorials4j.framework.examples.lock;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tutorials4j.framework.cache.redisson.lock.RedissonBlockLockable;
import tutorials4j.framework.cache.redisson.lock.RedissonReentrantLockable;

/**
 * 订单
 *
 * @author Yun Jiao
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderRedissonService {
  @RedissonBlockLockable(key = "#root.args[0]", prefix = "order:")
  public void blockNonExpireTime(String orderId) {
    log.info("blockNonExpireTime - {} - {}", Thread.currentThread().getName(), orderId);
    int time = sleep();
    log.info(
        "blockNonExpireTime - {} - {}, 时长：{}", Thread.currentThread().getName(), orderId, time);
  }

  @RedissonBlockLockable(key = "#root.args[0]", prefix = "order:", expireTime = 3000)
  public void blockExpireTime(String orderId) {
    log.info("blockExpireTime - {} - {}", Thread.currentThread().getName(), orderId);
    int time = sleep();
    log.info("blockExpireTime - {} - {}, 时长：{}", Thread.currentThread().getName(), orderId, time);
  }

  @RedissonReentrantLockable(key = "#root.args[0]", prefix = "order:")
  public void reentrantNonExpireTime(String orderId) {
    log.info("reentrantNonExpireTime - {} - {}", Thread.currentThread().getName(), orderId);
    int time = sleep();
    log.info(
        "reentrantNonExpireTime - {} - {}, 时长：{}", Thread.currentThread().getName(), orderId, time);
  }

  @RedissonReentrantLockable(key = "#root.args[0]", prefix = "order:", expireTime = 4000)
  public void reentrantExpireTime(String orderId) {
    log.info("reentrantFixedLease - {} - {}", Thread.currentThread().getName(), orderId);
    int time = sleep();
    log.info(
        "reentrantFixedLease - {} - {}, 时长：{}", Thread.currentThread().getName(), orderId, time);
  }

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
