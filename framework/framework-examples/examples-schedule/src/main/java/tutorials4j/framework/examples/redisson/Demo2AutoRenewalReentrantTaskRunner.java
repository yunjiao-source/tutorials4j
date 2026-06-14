package tutorials4j.framework.examples.redisson;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tutorials4j.framework.cache.core.exception.LockException;
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
    return "schedule:demo2";
  }

  @Override
  public Duration waitTime() {
    return Duration.ofSeconds(1);
  }

  @Override
  public void doRun(Map<String, String> params) {
    log.info(
        ">>> {}, {}, {}", Thread.currentThread().getName(), "demo2", System.currentTimeMillis());
    long milli = ThreadLocalRandom.current().nextInt(10000);
    try {
      TimeUnit.MILLISECONDS.sleep(milli);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void handleException(LockException exception) {
    log.error("DEMO2: {}", exception.getMessage());
  }
}
