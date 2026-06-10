package tutorials4j.framework.common.core;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
public class ExecutorServiceHolder<T extends ExecutorService> {
  private static final AtomicInteger THREAD_POOL_ID = new AtomicInteger(1);

  private final T instance;
  private final ExecutionOption option;

  protected ExecutorServiceHolder(T instance, ExecutionOption option) {
    this.instance = instance;
    this.option = option;
  }

  public T instance() {
    return instance;
  }

  public static ExecutorServiceHolder<ScheduledThreadPoolExecutor> buildScheduler(
      ExecutionOption option) {
    NamedThreadFactory threadFactory =
        new NamedThreadFactory(option.getThreadNamePrefix(), option.isDaemon());

    ScheduledThreadPoolExecutor executor =
        new ScheduledThreadPoolExecutor(
            option.getCorePoolSize(), threadFactory, option.getRejectedExecutionHandler());

    executor.setRemoveOnCancelPolicy(true);
    return new ExecutorServiceHolder<>(executor, option);
  }

  public static ExecutorServiceHolder<ThreadPoolExecutor> buildThreadPool(ExecutionOption option) {
    NamedThreadFactory threadFactory =
        new NamedThreadFactory(option.getThreadNamePrefix(), option.isDaemon());

    ThreadPoolExecutor executor =
        new ThreadPoolExecutor(
            option.getCorePoolSize(),
            option.getMaximumPoolSize(),
            option.getKeepAlive().toMillis(),
            TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(option.getQueueCapacity()),
            threadFactory,
            option.getRejectedExecutionHandler());

    if (option.isAllowCoreThreadTimeOut()) {
      executor.allowCoreThreadTimeOut(true);
    }

    return new ExecutorServiceHolder<>(executor, option);
  }

  public void shutdown() {
    if (option.isAwaitTermination()) {
      instance.shutdown(); // 拒绝新任务
      try {
        if (!instance.awaitTermination(
            option.getAwaitTerminationPeriod().toMillis(), TimeUnit.MILLISECONDS)) {
          log.warn("Executor did not terminate within timeout, forcing shutdown...");
          instance.shutdownNow(); // 超时则强制终止
        }
      } catch (InterruptedException e) {
        log.error("Await termination interrupted", e);
        instance.shutdownNow();
        Thread.currentThread().interrupt();
      }
    } else {
      log.debug("Force shutdown without awaiting termination");
      instance.shutdownNow();
    }
  }

  @RequiredArgsConstructor
  public static class NamedThreadFactory implements ThreadFactory {
    private final String threadNamePrefix;
    private final boolean daemon;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final int POOL_ID = THREAD_POOL_ID.getAndIncrement();

    @Override
    public Thread newThread(Runnable r) {
      String uniquePrefix = threadNamePrefix + POOL_ID + "-";
      Thread t = new Thread(r, uniquePrefix + threadNumber.getAndIncrement());
      t.setDaemon(daemon);
      return t;
    }
  }
}
