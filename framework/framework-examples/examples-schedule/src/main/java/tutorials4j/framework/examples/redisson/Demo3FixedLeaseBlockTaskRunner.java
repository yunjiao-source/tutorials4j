package tutorials4j.framework.examples.redisson;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tutorials4j.framework.schedule.redisson.FixedLeaseBlockLockTaskRunner;

/**
 * 固定租期阻塞锁任务示例三。
 *
 * <p>演示基于 Redisson 的固定租期（fixed lease）阻塞锁任务：锁持有固定时间（5 秒）后自动释放， 任务执行时长不得超过锁租期。
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
public class Demo3FixedLeaseBlockTaskRunner implements FixedLeaseBlockLockTaskRunner {

  /** 返回锁键 {@code fixed-lease-block:demo3}。 */
  @Override
  public String key() {
    return "fixed-lease-block:demo3";
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
