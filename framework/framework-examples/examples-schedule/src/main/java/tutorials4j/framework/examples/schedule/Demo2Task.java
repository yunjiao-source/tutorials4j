package tutorials4j.framework.examples.schedule;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tutorials4j.framework.schedule.core.bean.TaskRunner;

/**
 * 定时任务演示 2：模拟执行耗时任务，随机休眠 0~4 秒，并按一定概率抛出异常， 用于演示任务执行失败时的处理逻辑。
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
public class Demo2Task implements TaskRunner {

  /** {@inheritDoc} */
  @Override
  public void run(Map<String, String> params) {
    log.info(
        ">>> demo2, {}, {}, {}",
        Thread.currentThread().getName(),
        params,
        System.currentTimeMillis());
    long milli = ThreadLocalRandom.current().nextInt(4000);
    try {
      TimeUnit.MILLISECONDS.sleep(milli);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    int num = ThreadLocalRandom.current().nextInt(100);
    if (num >= 70) {
      throw new RuntimeException("任务异常");
    }
  }
}
