package tutorials4j.framework.common.core.util;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import tutorials4j.framework.common.core.ExecutionOption;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class CloseUtils {
  public static void close(ScheduledExecutorService scheduler, ExecutionOption executionOption) {
    if (executionOption.isAwaitTermination()) {
      scheduler.shutdown(); // 拒绝新任务
      try {
        if (!scheduler.awaitTermination(
            executionOption.getAwaitTerminationPeriod().toMillis(), TimeUnit.MILLISECONDS)) {
          scheduler.shutdownNow(); // 超时则强制终止
        }
      } catch (InterruptedException e) {
        scheduler.shutdownNow();
        Thread.currentThread().interrupt();
      }
    } else {
      scheduler.shutdownNow();
    }
  }
}
