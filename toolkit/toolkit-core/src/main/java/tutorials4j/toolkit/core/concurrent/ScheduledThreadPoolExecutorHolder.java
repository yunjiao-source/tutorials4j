package tutorials4j.toolkit.core.concurrent;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import tutorials4j.toolkit.core.autoconfigure.ScheduledExecutionOptions;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Slf4j
public class ScheduledThreadPoolExecutorHolder {
  public static final ScheduledThreadPoolExecutorHolder instance =
      new ScheduledThreadPoolExecutorHolder();

  private ScheduledExecutionOptions options;
  @Getter private volatile ScheduledThreadPoolExecutor executor;

  public synchronized void initExecutor(ScheduledExecutionOptions options) {
    Assert.notNull(options, "options must not be null");
    if (executor != null) {
      return;
    }

    this.options = ScheduledExecutionOptions.withDefaults(options);

    executor =
        new ScheduledThreadPoolExecutor(
            this.options.getCorePoolSize(),
            new SimpleNamedThreadFactory(
                this.options.getThreadNamePrefix(), this.options.getDaemon()),
            (r, e) -> {
              log.warn(
                  "Scheduled task rejected. poolSize={}, activeCount={}, queueSize={}, isShutdown={}, task={}",
                  e.getPoolSize(),
                  e.getActiveCount(),
                  e.getQueue().size(),
                  e.isShutdown(),
                  r.toString());
              throw new RejectedExecutionException("Scheduled task " + r + " rejected from " + e);
            });
    executor.setRemoveOnCancelPolicy(true);
    executor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
    executor.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
  }

  public synchronized void shutdown() {
    if (executor == null) {
      return;
    }

    if (log.isDebugEnabled()) {
      log.debug(
          "关闭计划任务线程池，hashcode={}, class={}, threadNamePrefix={}",
          this,
          executor.getClass().getSimpleName(),
          options.getThreadNamePrefix());
    }

    if (options.getAwaitTermination()) {
      executor.shutdown(); // 拒绝新任务
      try {
        if (!executor.awaitTermination(
            options.getAwaitTerminationPeriod().toMillis(), TimeUnit.MILLISECONDS)) {
          log.trace("Executor did not terminate within timeout, forcing shutdown...");
          executor.shutdownNow(); // 超时则强制终止
        }
      } catch (InterruptedException e) {
        log.error("等待线程池终结异常", e);
        executor.shutdownNow();
        Thread.currentThread().interrupt();
      }
    } else {
      log.trace("Force shutdown without awaiting termination");
      executor.shutdownNow();
    }
  }
}
