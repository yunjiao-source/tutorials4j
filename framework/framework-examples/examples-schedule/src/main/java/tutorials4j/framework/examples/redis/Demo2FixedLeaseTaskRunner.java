package tutorials4j.framework.examples.redis;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tutorials4j.framework.schedule.redis.FixedLeaseLockTaskRunner;

/**
 * 固定租期锁调度任务示例。
 *
 * <p>演示基于 Redis 固定租期锁的 {@link FixedLeaseLockTaskRunner} 实现：锁在固定租期内有效， 执行时模拟随机耗时的业务处理。
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
public class Demo2FixedLeaseTaskRunner implements FixedLeaseLockTaskRunner {

  /** 返回任务的唯一键。 */
  @Override
  public String key() {
    return "schedule:demo2";
  }

  /** 返回锁的固定租期时长。 */
  @Override
  public Duration expireTime() {
    return Duration.ofSeconds(3);
  }

  /** 模拟执行任务：记录日志并随机休眠一段时间。 */
  @Override
  public void doRun(Map<String, String> params) {
    log.info(">>> {}, {}, {}", key(), Thread.currentThread().getName(), System.currentTimeMillis());
    long milli = ThreadLocalRandom.current().nextInt(10000);
    try {
      TimeUnit.MILLISECONDS.sleep(milli);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  /** 记录任务执行过程中的异常。 */
  @Override
  public void handleException(Exception exception) {
    log.error("{}: {}", key(), exception.getMessage());
  }
}
