package tutorials4j.framework.common.core;

import java.time.Duration;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.Data;

/**
 * 线程池执行配置选项。
 *
 * <p>定义线程池的核心参数：线程数、线程名称、空闲存活时间、任务队列容量、拒绝策略与关闭行为等， 用于统一构建框架内的线程池。
 *
 * @author Yun Jiao
 */
@Data
public class ExecutionOption {
  /** 核心线程数，默认 4 */
  private int corePoolSize = 4;

  /** 最大线程数，默认 8 */
  private int maximumPoolSize = 8;

  /** 线程名称前缀，默认 "t4j-thread-pool-" */
  private String threadNamePrefix = "t4j-thread-pool-";

  /** 是否创建守护线程，默认 false */
  private boolean daemon;

  /** 是否允许核心线程超时，默认 false 若设为 true，核心线程空闲超过 keepAliveTime 会被回收 */
  private boolean allowCoreThreadTimeOut = false;

  /** 线程空闲存活时间（秒），默认 60 当 allowCoreThreadTimeOut = true 时生效 */
  private Duration keepAlive = Duration.ofSeconds(60);

  /** 任务队列容量，默认 100 */
  // 不要设置太大，如果队列已满，且当前线程数 < maximumPoolSize，创建新线程执行任务
  private int queueCapacity = 100;

  /**
   * 拒绝策略名称，可选： - AbortPolicy（默认）：抛出 RejectedExecutionException - CallerRunsPolicy：由调用线程执行任务 -
   * DiscardPolicy：直接丢弃任务 - DiscardOldestPolicy：丢弃队列头部任务，重试提交当前任务
   */
  private RejectedPolicy rejectedPolicy = RejectedPolicy.ABORT;

  /** 关闭线程池时是否等待任务执行完毕，默认 true */
  private boolean awaitTermination = true;

  /** Maximum time the executor should wait for remaining tasks to complete. */
  private Duration awaitTerminationPeriod = Duration.ofSeconds(30);

  /** 根据配置名称获取拒绝策略实例 */
  public RejectedExecutionHandler getRejectedExecutionHandler() {
    return switch (rejectedPolicy) {
      case CALLER_RUNS -> new ThreadPoolExecutor.CallerRunsPolicy();
      case DISCARD -> new ThreadPoolExecutor.DiscardPolicy();
      case DISCARD_OLDEST -> new ThreadPoolExecutor.DiscardOldestPolicy();
      default -> new ThreadPoolExecutor.AbortPolicy();
    };
  }

  /**
   * 线程池拒绝策略。
   *
   * @author Yun Jiao
   */
  public enum RejectedPolicy {
    /** 抛出 RejectedExecutionException 拒绝任务（默认） */
    ABORT,
    /** 由调用线程执行被拒绝的任务 */
    CALLER_RUNS,
    /** 直接丢弃被拒绝的任务 */
    DISCARD,
    /** 丢弃队列头部的任务并重试提交当前任务 */
    DISCARD_OLDEST;
  }
}
