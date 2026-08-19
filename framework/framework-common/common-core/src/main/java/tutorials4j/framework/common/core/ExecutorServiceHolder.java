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
 * 线程池持有者，包装一个线程池实例及其执行配置。
 *
 * <p>提供基于 {@link ExecutionOption} 构建定时线程池与普通线程池的工厂方法，以及优雅关闭线程池的能力。
 *
 * @param <T> 持有的线程池类型
 * @author Yun Jiao
 */
@Slf4j
public class ExecutorServiceHolder<T extends ExecutorService> {
  private static final AtomicInteger THREAD_POOL_ID = new AtomicInteger(1);

  private final T instance;
  private final ExecutionOption option;

  /**
   * 使用线程池实例与执行配置构造持有者。
   *
   * @param instance 线程池实例
   * @param option 执行配置
   */
  protected ExecutorServiceHolder(T instance, ExecutionOption option) {
    this.instance = instance;
    this.option = option;
  }

  /**
   * 获取持有的线程池实例。
   *
   * @return 线程池实例
   */
  public T instance() {
    return instance;
  }

  /**
   * 根据配置构建一个定时线程池。
   *
   * @param option 执行配置
   * @return 持有定时线程池的 {@link ExecutorServiceHolder}
   */
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

  /**
   * 根据配置构建一个普通线程池。
   *
   * @param option 执行配置
   * @return 持有线程池的 {@link ExecutorServiceHolder}
   */
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

  /**
   * 优雅关闭线程池。
   *
   * <p>根据配置决定是否等待任务执行完毕；等待超时或线程被中断时强制终止线程池。
   */
  public void shutdown() {
    if (log.isDebugEnabled()) {
      log.debug(
          "关闭线程池，class={}, threadNamePrefix={}",
          instance.getClass().getSimpleName(),
          option.getThreadNamePrefix());
    }
    if (option.isAwaitTermination()) {
      instance.shutdown(); // 拒绝新任务
      try {
        if (!instance.awaitTermination(
            option.getAwaitTerminationPeriod().toMillis(), TimeUnit.MILLISECONDS)) {
          log.trace("Executor did not terminate within timeout, forcing shutdown...");
          instance.shutdownNow(); // 超时则强制终止
        }
      } catch (InterruptedException e) {
        log.error("等待线程池终结异常", e);
        instance.shutdownNow();
        Thread.currentThread().interrupt();
      }
    } else {
      log.trace("[COMMON-CORE] Force shutdown without awaiting termination");
      instance.shutdownNow();
    }
  }

  /**
   * 命名线程工厂，为线程池中的线程生成带唯一前缀与序号的名称。
   *
   * @author Yun Jiao
   */
  @RequiredArgsConstructor
  public static class NamedThreadFactory implements ThreadFactory {
    private final String threadNamePrefix;
    private final boolean daemon;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final int POOL_ID = THREAD_POOL_ID.getAndIncrement();

    /**
     * 创建并命名一个新线程。
     *
     * @param r 线程要执行的任务
     * @return 新创建的线程
     */
    @Override
    public Thread newThread(Runnable r) {
      String uniquePrefix = threadNamePrefix + POOL_ID + "-";
      Thread t = new Thread(r, uniquePrefix + threadNumber.getAndIncrement());
      t.setDaemon(daemon);
      return t;
    }
  }
}
