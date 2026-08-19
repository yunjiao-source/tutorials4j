package tutorials4j.framework.examples.redisson;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tutorials4j.framework.schedule.redisson.FixedLeaseReentrantLockTaskRunner;

/**
 * 固定租期可重入锁任务示例四。
 *
 * <p>演示基于 Redisson 的固定租期（fixed lease）可重入锁任务：支持同一线程重入，锁持有固定时间 （5 秒）后自动释放，获取锁时最多等待 1 秒。
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
public class Demo4FixedLeaseReentrantTaskRunner implements FixedLeaseReentrantLockTaskRunner {

  /** 返回锁键 {@code fixed-lease-reentrant:demo4}。 */
  @Override
  public String key() {
    return "fixed-lease-reentrant:demo4";
  }

  /** 返回获取锁的最大等待时间 1 秒。 */
  @Override
  public Duration waitTime() {
    return Duration.ofSeconds(1);
  }

  /** 返回锁的固定租期 5 秒。 */
  @Override
  public Duration expireTime() {
    return Duration.ofSeconds(5);
  }

  /** 模拟耗时业务逻辑：随机休眠 0~10 秒并打印执行日志。 */
  @Override
  public void doRun(Map<String, String> params) {
    log.info(">>> {}, {}, {}", key(), Thread.currentThread().getName(), System.currentTimeMillis());
    long milli = ThreadLocalRandom.current().nextInt(10000);
    try {
      TimeUnit.MILLISECONDS.sleep(milli);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    log.info("<<< {}, {}, {}", key(), Thread.currentThread().getName(), System.currentTimeMillis());
  }

  /** 记录任务执行过程中的异常日志。 */
  @Override
  public void handleException(Exception exception) {
    log.error("{}: {}", key(), exception.getMessage());
  }
}
