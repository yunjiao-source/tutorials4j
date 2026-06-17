package tutorials4j.framework.examples.schedule;

import java.time.Duration;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tutorials4j.framework.cache.core.exception.LockException;
import tutorials4j.framework.schedule.redis.FixedLeaseLockTaskRunner;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
public class Demo5FixedLeaseTaskRunner implements FixedLeaseLockTaskRunner {

  @Override
  public String key() {
    return "schedule:" + this.getClass().getSimpleName();
  }

  @Override
  public Duration expireTime() {
    return Duration.ofSeconds(3);
  }

  @Override
  public void doRun(Map<String, String> params) {
    log.info(
        ">>> {}, {}, {}",
        Thread.currentThread().getName(),
        this.getClass().getSimpleName(),
        System.currentTimeMillis());

    Utils.sleep();
  }

  @Override
  public void handleException(LockException exception) {
    log.error(this.getClass().getSimpleName() + ": {}", exception.getMessage());
  }
}
