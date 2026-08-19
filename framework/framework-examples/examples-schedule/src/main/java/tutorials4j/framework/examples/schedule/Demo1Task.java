package tutorials4j.framework.examples.schedule;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tutorials4j.framework.schedule.core.bean.TaskRunner;

/**
 * 定时任务演示 1：模拟执行耗时任务，随机休眠 0~3 秒后结束。
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
public class Demo1Task implements TaskRunner {

  /** {@inheritDoc} */
  @Override
  public void run(Map<String, String> params) {
    log.info(
        ">>> demo1, {}, {}, {}",
        Thread.currentThread().getName(),
        params,
        System.currentTimeMillis());
    long milli = ThreadLocalRandom.current().nextInt(3000);
    try {
      TimeUnit.MILLISECONDS.sleep(milli);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
