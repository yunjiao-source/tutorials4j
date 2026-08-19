package tutorials4j.framework.examples.schedule;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tutorials4j.framework.schedule.core.bean.TaskRunner;

/**
 * 定时任务演示 3：模拟执行耗时任务，随机休眠 0~5 秒后结束。
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
public class Demo3Task implements TaskRunner {

  /** {@inheritDoc} */
  @Override
  public void run(Map<String, String> params) {
    log.info(
        ">>> demo3, {}, {}, {}",
        Thread.currentThread().getName(),
        params,
        System.currentTimeMillis());
    long milli = ThreadLocalRandom.current().nextInt(5000);
    try {
      TimeUnit.MILLISECONDS.sleep(milli);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
