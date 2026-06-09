package tutorials4j.framework.examples.lock;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tutorials4j.framework.cache.core.lock.LocalLockable;

/**
 * 订单
 *
 * @author Yun Jiao
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderLocalService {
  @LocalLockable(key = "#root.args[0]", prefix = "order:", waitTime = -1)
  public void nonWaitTime(String orderId) {
    log.info("nonWaitTime - {} - {}", Thread.currentThread().getName(), orderId);
    int time = sleep();
    log.info("nonWaitTime - {} - {}, 时长：{}", Thread.currentThread().getName(), orderId, time);
  }

  @LocalLockable(key = "#root.args[0]", prefix = "order:")
  public void waitTime(String orderId) {
    log.info("waitTime - {} - {}", Thread.currentThread().getName(), orderId);
    int time = sleep();
    log.info("waitTime - {} - {}, 时长：{}", Thread.currentThread().getName(), orderId, time);
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
