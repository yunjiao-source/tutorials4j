package tutorials4j.framework.web.logging.mdc;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

/**
 * 任务装饰器，用于在异步任务执行前后复制并恢复 MDC 上下文。
 *
 * <p>当使用 Spring 的 @Async 或线程池执行异步任务时，子线程无法自动继承父线程的 MDC。 该装饰器在提交任务时捕获父线程的 MDC 快照，并在子线程执行前恢复该上下文，
 * 执行后清理，从而保证异步任务中的日志也能输出正确的链路追踪标识。
 *
 * @author Yun Jiao
 * @see org.springframework.core.task.TaskDecorator
 * @see org.slf4j.MDC
 */
@Slf4j
public class TraceTaskDecorator implements TaskDecorator {
  /**
   * 装饰异步任务，在任务执行前恢复父线程的 MDC 上下文，执行完成后清理 MDC。
   *
   * @param runnable 原始任务
   * @return 带 MDC 上下文复制与清理逻辑的装饰任务
   */
  @Override
  public Runnable decorate(Runnable runnable) {
    // 复制当前线程的MDC上下文
    Map<String, String> contextMap = MDC.getCopyOfContextMap();
    return () -> {
      try {
        // 异步任务执行前设置MDC
        if (contextMap != null) {
          MDC.setContextMap(contextMap);
        }
        if (log.isDebugEnabled()) {
          log.debug("跟踪信息, MDC={}", contextMap);
        }
        runnable.run();
      } finally {
        MDC.clear();
      }
    };
  }
}
