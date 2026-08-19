package tutorials4j.framework.examples.redisson;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tutorials4j.framework.schedule.redisson.AutoRenewalBlockLockTaskRunner;

/**
 * 自动续期阻塞锁任务示例一。
 *
 * <p>演示基于 Redisson 的自动续期（看门狗）阻塞锁任务：任务执行期间自动续期锁，直至执行完成释放锁。
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
public class Demo1AutoRenewalBlockTaskRunner implements AutoRenewalBlockLockTaskRunner {

  /** 返回锁键 {@code auto-renewal-block:demo1}。 */
  @Override
  public String key() {
    return "auto-renewal-block:demo1";
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
