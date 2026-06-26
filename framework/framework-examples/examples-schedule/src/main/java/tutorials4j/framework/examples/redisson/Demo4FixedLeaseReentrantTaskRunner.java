package tutorials4j.framework.examples.redisson;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tutorials4j.framework.common.core.exception.BaseRuntimeException;
import tutorials4j.framework.schedule.redisson.FixedLeaseReentrantLockTaskRunner;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
public class Demo4FixedLeaseReentrantTaskRunner implements FixedLeaseReentrantLockTaskRunner {

  @Override
  public String key() {
    return "schedule:demo4";
  }

  @Override
  public Duration waitTime() {
    return Duration.ofSeconds(1);
  }

  @Override
  public Duration expireTime() {
    return Duration.ofSeconds(5);
  }

  @Override
  public void doRun(Map<String, String> params) {
    log.info(
        ">>> {}, {}, {}", Thread.currentThread().getName(), "demo4", System.currentTimeMillis());
    long milli = ThreadLocalRandom.current().nextInt(10000);
    try {
      TimeUnit.MILLISECONDS.sleep(milli);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void handleException(BaseRuntimeException exception) {
    log.error("DEMO4: {}", exception.getMessage());
  }
}
