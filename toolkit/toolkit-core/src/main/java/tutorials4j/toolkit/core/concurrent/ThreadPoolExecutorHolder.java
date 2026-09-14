package tutorials4j.toolkit.core.concurrent;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import java.time.Duration;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import tutorials4j.toolkit.core.autoconfigure.ToolkitProperties.ThreadExecutionOptions;

/**
 * TODO
 *
 * @author Yun Jiao
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

    int core = Runtime.getRuntime().availableProcessors() + 1;
    ThreadExecutionOptions mergeOptions =
        new ThreadExecutionOptions(
            false,
            core,
            "thread-pool-",
            true,
            Duration.ofSeconds(30),
            core * 2,
            1000,
            true,
            Duration.ofHours(1));
    BeanUtil.copyProperties(options, mergeOptions, CopyOptions.create().setIgnoreNullValue(true));
    this.options = mergeOptions;

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
                  "Thread task rejected. options={}, poolSize={}, activeCount={}, queueSize={}, isShutdown={}, task={}",
                  mergeOptions,
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
