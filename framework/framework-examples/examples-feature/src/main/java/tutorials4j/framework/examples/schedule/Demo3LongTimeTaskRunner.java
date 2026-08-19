package tutorials4j.framework.examples.schedule;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tutorials4j.framework.schedule.core.bean.TaskRunner;

/**
 * 演示长时间运行定时任务的示例，随机休眠最长 30 秒以模拟耗时任务。
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
public class Demo3LongTimeTaskRunner implements TaskRunner {

  /** 随机休眠 0-30 秒，模拟长时间执行的任务。 */
  @Override
  public void run(Map<String, String> params) {
    log.info(
        ">>> {}, {}, {}",
        Thread.currentThread().getName(),
        this.getClass().getSimpleName(),
        System.currentTimeMillis());

    long milli = ThreadLocalRandom.current().nextInt(30000);
    try {
      TimeUnit.MILLISECONDS.sleep(milli);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
