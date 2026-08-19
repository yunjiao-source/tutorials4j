package tutorials4j.framework.examples.redis;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tutorials4j.framework.schedule.redis.AutoRenewalLockTaskRunner;

/**
 * 自动续期锁调度任务示例。
 *
 * <p>演示基于 Redis 自动续期锁的 {@link AutoRenewalLockTaskRunner} 实现：任务执行期间自动续期锁， 执行时模拟随机耗时的业务处理。
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
public class Demo1AutoRenewalTaskRunner implements AutoRenewalLockTaskRunner {

  /** 返回任务的唯一键。 */
  @Override
  public String key() {
    return "schedule:demo1";
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
