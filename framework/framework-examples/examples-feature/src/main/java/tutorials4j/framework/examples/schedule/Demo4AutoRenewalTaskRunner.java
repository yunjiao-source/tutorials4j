package tutorials4j.framework.examples.schedule;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tutorials4j.framework.common.core.exception.BaseRuntimeException;
import tutorials4j.framework.schedule.redis.AutoRenewalLockTaskRunner;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
public class Demo4AutoRenewalTaskRunner implements AutoRenewalLockTaskRunner {

  @Override
  public String key() {
    return "schedule:" + this.getClass().getSimpleName();
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
  public void handleException(BaseRuntimeException exception) {
    log.error(this.getClass().getSimpleName() + ": {}", exception.getMessage());
  }
}
