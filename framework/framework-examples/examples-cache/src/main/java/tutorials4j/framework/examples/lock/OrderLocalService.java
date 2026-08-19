package tutorials4j.framework.examples.lock;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tutorials4j.framework.cache.core.lock.LocalLockable;

/**
 * 订单本地锁示例服务。
 *
 * <p>通过 {@code @LocalLockable} 注解演示基于本地锁（进程内锁）的互斥控制，包括不等待与等待两种加锁模式。
 *
 * @author Yun Jiao
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderLocalService {
  /**
   * 不等待模式的本地锁示例：获取不到锁时立即失败，不做等待。
   *
   * @param orderId 订单编号
   */
  @LocalLockable(key = "#root.args[0]", prefix = "order:", waitTime = -1)
  public void nonWaitTime(String orderId) {
    log.info("nonWaitTime - {} - {}", Thread.currentThread().getName(), orderId);
    int time = sleep();
    log.info("nonWaitTime - {} - {}, 时长：{}", Thread.currentThread().getName(), orderId, time);
  }

  /**
   * 等待模式的本地锁示例：获取不到锁时等待锁释放后再执行业务。
   *
   * @param orderId 订单编号
   */
  @LocalLockable(key = "#root.args[0]", prefix = "order:")
  public void waitTime(String orderId) {
    log.info("waitTime - {} - {}", Thread.currentThread().getName(), orderId);
    int time = sleep();
    log.info("waitTime - {} - {}, 时长：{}", Thread.currentThread().getName(), orderId, time);
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
