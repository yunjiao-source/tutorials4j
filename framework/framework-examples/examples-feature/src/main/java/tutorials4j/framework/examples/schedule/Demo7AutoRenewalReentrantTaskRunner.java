package tutorials4j.framework.examples.schedule;

import java.time.Duration;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tutorials4j.framework.schedule.redisson.AutoRenewalReentrantLockTaskRunner;

/**
 * 演示基于 Redisson 自动续期可重入锁的定时任务示例，同一线程可重复加锁且锁租期自动续期。
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
public class Demo7AutoRenewalReentrantTaskRunner implements AutoRenewalReentrantLockTaskRunner {

  /** 返回获取锁的最长等待时间（4 秒）。 */
  @Override
  public Duration waitTime() {
    return Duration.ofSeconds(4);
  }

  /** 返回该任务对应的分布式锁键。 */
  @Override
  public String key() {
    return "schedule:" + this.getClass().getSimpleName();
  }

  /** 执行任务主体逻辑，记录日志并随机休眠。 */
  @Override
  public void doRun(Map<String, String> params) {
    log.info(
        ">>> {}, {}, {}",
        Thread.currentThread().getName(),
        this.getClass().getSimpleName(),
        System.currentTimeMillis());

    Utils.sleep();
  }

  /** 记录任务执行过程中捕获的异常日志。 */
  @Override
  public void handleException(Exception exception) {
    log.error(this.getClass().getSimpleName() + ": {}", exception.getMessage());
  }
}
