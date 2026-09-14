package tutorials4j.toolkit.core.concurrent;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import java.time.Duration;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import tutorials4j.toolkit.core.autoconfigure.ToolkitProperties.ScheduledExecutionOptions;

/**
 * TODO
 *
 * @author Yun Jiao
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

    int core = Runtime.getRuntime().availableProcessors() + 1;
    ScheduledExecutionOptions mergeOptions =
        new ScheduledExecutionOptions(false, core, "schedule-pool-", true, Duration.ofSeconds(30));
    BeanUtil.copyProperties(options, mergeOptions, CopyOptions.create().setIgnoreNullValue(true));
    this.options = mergeOptions;

    executor =
        new ScheduledThreadPoolExecutor(
            this.options.getCorePoolSize(),
            new SimpleNamedThreadFactory(
                this.options.getThreadNamePrefix(), this.options.getDaemon()),
            (r, e) -> {
              log.warn(
                  "Scheduled task rejected. options={}, poolSize={}, activeCount={}, queueSize={}, isShutdown={}, task={}",
                  mergeOptions,
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
