package tutorials4j.framework.tenant.core;

import org.springframework.core.task.TaskDecorator;
import tutorials4j.framework.common.core.TenantContextHolder;

/**
 * 租户多线程任务装饰器：在异步任务执行前写入当前租户上下文，执行结束后清除，确保异步任务中租户上下文正确传递。
 *
 * @author Yun Jiao
 */
public class TenantTaskDecorator implements TaskDecorator {

  /**
   * 装饰任务：执行前设置租户上下文，任务执行完毕后清除。
   *
   * @param runnable 原始任务
   * @return 携带租户上下文传递逻辑的装饰任务
   */
  @Override
  public Runnable decorate(Runnable runnable) {
    String tenant = TenantContextHolder.get();
    return () -> {
      try {
        // 设置租户
        TenantContextHolder.set(tenant);
        runnable.run();
      } finally {
        TenantContextHolder.clear();
      }
    };
  }
}
