package tutorials4j.framework.examples.redis;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tutorials4j.framework.cache.core.exception.LockException;
import tutorials4j.framework.schedule.redis.AutoRenewalLockTaskRunner;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
public class Demo1AutoRenewalTaskRunner implements AutoRenewalLockTaskRunner {

  @Override
  public String key() {
    return "schedule:demo1";
  }

  @Override
  public void doRun(Map<String, String> params) {
    log.info(
        ">>> {}, {}, {}", Thread.currentThread().getName(), params, System.currentTimeMillis());
    long milli = ThreadLocalRandom.current().nextInt(10000);
    try {
      TimeUnit.MILLISECONDS.sleep(milli);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void handleException(LockException exception) {
    log.error("DEMO1: {}", exception.getMessage());
  }
}
