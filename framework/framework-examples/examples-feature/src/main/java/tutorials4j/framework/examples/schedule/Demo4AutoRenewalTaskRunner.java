package tutorials4j.framework.examples.schedule;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tutorials4j.framework.schedule.redis.AutoRenewalLockTaskRunner;

/**
 * 演示基于 Redis 自动续期锁的定时任务示例，锁租期在任务执行期间自动续期，避免任务未完成时锁提前过期。
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
public class Demo4AutoRenewalTaskRunner implements AutoRenewalLockTaskRunner {

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
