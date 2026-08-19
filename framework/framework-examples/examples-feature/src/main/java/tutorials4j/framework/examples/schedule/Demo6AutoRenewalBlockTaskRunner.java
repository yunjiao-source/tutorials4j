package tutorials4j.framework.examples.schedule;

import java.time.Duration;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tutorials4j.framework.schedule.redisson.AutoRenewalBlockLockTaskRunner;

/**
 * 演示基于 Redisson 自动续期阻塞锁的定时任务示例，获取锁失败时阻塞等待直至成功。
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
public class Demo6AutoRenewalBlockTaskRunner implements AutoRenewalBlockLockTaskRunner {

  /** 返回该任务对应的分布式锁键。 */
  @Override
  public String key() {
    return "schedule:" + this.getClass().getSimpleName();
  }

  /** 返回锁的初始租期（3 秒），执行期间自动续期。 */
  @Override
  public Duration expireTime() {
    return Duration.ofSeconds(3);
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
