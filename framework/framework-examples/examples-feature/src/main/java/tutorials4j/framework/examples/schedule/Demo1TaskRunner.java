package tutorials4j.framework.examples.schedule;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tutorials4j.framework.schedule.core.bean.TaskRunner;

/**
 * 演示普通定时任务执行器的示例任务，每次执行时打印当前线程、类名与时间戳并随机休眠。
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
public class Demo1TaskRunner implements TaskRunner {

  /** 执行任务，记录日志并随机休眠以模拟任务耗时。 */
  @Override
  public void run(Map<String, String> params) {
    log.info(
        ">>> {}, {}, {}",
        Thread.currentThread().getName(),
        this.getClass().getSimpleName(),
        System.currentTimeMillis());
    Utils.sleep();
  }
}
