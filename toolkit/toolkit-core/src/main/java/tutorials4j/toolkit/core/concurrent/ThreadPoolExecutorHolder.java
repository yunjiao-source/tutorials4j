package tutorials4j.toolkit.core.concurrent;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import tutorials4j.toolkit.core.autoconfigure.ThreadExecutionOptions;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Slf4j
public class ThreadPoolExecutorHolder {
  public static final ThreadPoolExecutorHolder instance = new ThreadPoolExecutorHolder();

  private ThreadExecutionOptions options;
  @Getter private volatile ThreadPoolExecutor executor;

  public synchronized void initExecutor(ThreadExecutionOptions options) {
    Assert.notNull(options, "options must not be null");
    if (executor != null) {
      return;
    }

    this.options = ThreadExecutionOptions.withDefaults(options);

    executor =
        new ThreadPoolExecutor(
            this.options.getCorePoolSize(),
            this.options.getMaximumPoolSize(),
            this.options.getKeepAlive().toMillis(),
            TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<Runnable>(this.options.getQueueCapacity()),
            new SimpleNamedThreadFactory(
                this.options.getThreadNamePrefix(), this.options.getDaemon()),
            (r, e) -> {
              log.warn(
                  "Thread task rejected. poolSize={}, activeCount={}, queueSize={}, isShutdown={}, task={}",
                  e.getPoolSize(),
                  e.getActiveCount(),
                  e.getQueue().size(),
                  e.isShutdown(),
                  r.toString());
              throw new RejectedExecutionException("Thread task " + r + " rejected from " + e);
            });
  }

  public synchronized void shutdown() {
    if (executor == null) {
      return;
    }

    if (log.isDebugEnabled()) {
      log.debug(
          "关闭线程池，hashcode={}, class={}, threadNamePrefix={}",
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
