package tutorials4j.framework.examples.redisson;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tutorials4j.framework.schedule.redisson.AutoRenewalReentrantLockTaskRunner;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
public class Demo2AutoRenewalReentrantTaskRunner implements AutoRenewalReentrantLockTaskRunner {

  @Override
  public String key() {
    return "auto-renewal-reentrant:demo2";
  }

  @Override
  public Duration waitTime() {
    return Duration.ofSeconds(1);
  }

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

  @Override
  public void handleException(Exception exception) {
    log.error("{}: {}", key(), exception.getMessage());
  }
}
