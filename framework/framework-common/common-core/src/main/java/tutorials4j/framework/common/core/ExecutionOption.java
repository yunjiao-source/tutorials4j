package tutorials4j.framework.common.core;

import java.time.Duration;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.Data;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
public class ExecutionOption {
  /** 核心线程数，默认 1 */
  private int corePoolSize = 1;

  /** 是否守护线程，默认 false */
  private boolean daemon = false;

  /** 是否允许核心线程超时，默认 false 若设为 true，核心线程空闲超过 keepAliveTime 会被回收 */
  private boolean allowCoreThreadTimeOut = false;

  /** 线程空闲存活时间（秒），默认 60 当 allowCoreThreadTimeOut = true 时生效 */
  private long keepAliveSeconds = 60;

  /**
   * 拒绝策略名称，可选： - AbortPolicy（默认）：抛出 RejectedExecutionException - CallerRunsPolicy：由调用线程执行任务 -
   * DiscardPolicy：直接丢弃任务 - DiscardOldestPolicy：丢弃队列头部任务，重试提交当前任务
   */
  private String rejectedPolicy = "AbortPolicy";

  private boolean awaitTermination = false;

  /** Maximum time the executor should wait for remaining tasks to complete. */
  private Duration awaitTerminationPeriod = Duration.ofSeconds(30);

  /** 根据配置名称获取拒绝策略实例 */
  public RejectedExecutionHandler getRejectedExecutionHandler() {
    return switch (rejectedPolicy) {
      case "CallerRunsPolicy" -> new ThreadPoolExecutor.CallerRunsPolicy();
      case "DiscardPolicy" -> new ThreadPoolExecutor.DiscardPolicy();
      case "DiscardOldestPolicy" -> new ThreadPoolExecutor.DiscardOldestPolicy();
      default -> new ThreadPoolExecutor.AbortPolicy();
    };
  }
}
