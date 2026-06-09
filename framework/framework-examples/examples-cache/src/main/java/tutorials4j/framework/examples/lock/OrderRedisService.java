package tutorials4j.framework.examples.lock;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tutorials4j.framework.cache.redis.lock.RedisLockable;

/**
 * 订单
 *
 * @author Yun Jiao
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderRedisService {
  @RedisLockable(key = "#root.args[0]", prefix = "order:")
  public void nonExpireTime(String orderId) {
    log.info("nonExpireTime - {} - {}", Thread.currentThread().getName(), orderId);
    int time = sleep();
    log.info("nonExpireTime - {} - {}, 时长：{}", Thread.currentThread().getName(), orderId, time);
  }

  @RedisLockable(key = "#root.args[0]", prefix = "order:", expireTime = 3000)
  public void expireTime(String orderId) {
    log.info("expireTime - {} - {}", Thread.currentThread().getName(), orderId);
    int time = sleep();
    log.info("expireTime - {} - {}, 时长：{}", Thread.currentThread().getName(), orderId, time);
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
