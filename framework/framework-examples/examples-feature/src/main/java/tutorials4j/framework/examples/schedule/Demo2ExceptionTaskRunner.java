package tutorials4j.framework.examples.schedule;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tutorials4j.framework.schedule.core.bean.TaskRunner;

/**
 * 演示定时任务执行过程中抛出异常的示例，约 30% 的概率抛出运行时异常，用于演示任务异常处理机制。
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
public class Demo2ExceptionTaskRunner implements TaskRunner {

  /** 执行任务并随机抛出运行时异常，用于演示任务异常处理机制。 */
  @Override
  public void run(Map<String, String> params) {
    log.info(
        ">>> {}, {}, {}",
        Thread.currentThread().getName(),
        this.getClass().getSimpleName(),
        System.currentTimeMillis());

    Utils.sleep();

    int num = ThreadLocalRandom.current().nextInt(100);
    if (num >= 70) {
      throw new RuntimeException("任务异常");
    }
  }
}
