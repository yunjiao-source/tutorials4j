package tutorials4j.framework.schedule.xxljob;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;

/**
 * XXL-JOB 执行器定制器函数式接口。
 *
 * <p>用于在 {@link XxlJobSpringExecutor} 创建完成后对其做进一步的自定义配置，可配合 自动配置按优先级依次应用。
 *
 * @author Yun Jiao
 */
@FunctionalInterface
public interface XxlJobSpringExecutorCustomizer {
  /**
   * 定制 XXL-JOB 执行器。
   *
   * @param executor 待定制的执行器实例
   */
  void customize(XxlJobSpringExecutor executor);
}
