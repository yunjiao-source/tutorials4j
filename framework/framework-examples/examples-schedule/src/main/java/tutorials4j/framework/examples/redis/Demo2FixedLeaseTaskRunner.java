package tutorials4j.framework.examples.redis;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tutorials4j.framework.common.core.exception.BaseRuntimeException;
import tutorials4j.framework.schedule.redis.FixedLeaseLockTaskRunner;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
public class Demo2FixedLeaseTaskRunner implements FixedLeaseLockTaskRunner {

  @Override
  public String key() {
    return "schedule:demo2";
  }

  @Override
  public Duration expireTime() {
    return Duration.ofSeconds(3);
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
  }

  @Override
  public void handleException(BaseRuntimeException exception) {
    log.error("{}: {}", key(), exception.getMessage());
  }
}
