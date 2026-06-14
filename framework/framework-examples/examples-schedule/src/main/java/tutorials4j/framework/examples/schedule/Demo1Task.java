package tutorials4j.framework.examples.schedule;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tutorials4j.framework.schedule.core.bean.TaskRunner;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
public class Demo1Task implements TaskRunner {

  @Override
  public void run(Map<String, String> params) {
    log.info(
        ">>> {}, {}, {}", Thread.currentThread().getName(), params, System.currentTimeMillis());
    long milli = ThreadLocalRandom.current().nextInt(3000);
    try {
      TimeUnit.MILLISECONDS.sleep(milli);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
